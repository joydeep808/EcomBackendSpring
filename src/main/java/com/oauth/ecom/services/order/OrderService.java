package com.oauth.ecom.services.order;

import java.time.LocalDateTime;
import java.util.*;

import org.json.JSONObject;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauth.ecom.dto.order.OrderDetailsSendDTO;
import com.oauth.ecom.entity.*;
import com.oauth.ecom.entity.enumentity.*;
import com.oauth.ecom.rabbitmq.MessageSender;
import com.oauth.ecom.rabbitmq.RabbitMqConfig;
import com.oauth.ecom.repository.*;
import com.oauth.ecom.services.cart.CartUtilService;
// import com.oauth.ecom.services.kafka.KafkaService;
import com.oauth.ecom.util.*;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final CartUtilService cartUtilService;
  private JwtInterceptor jwtInterceptor;
  private ProductRepo productRepo;
  private CartRepo cartRepo;
  private CartItemsRepo cartItemsRepo;
  private UserRepo userRepo;
  private OrderRepo orderRepo;
  private OrderItemsRepo orderItemsRepo;
  private AddressRepo addressRepo;
  private CouponCodeRepo couponCodeRepo;
  private final MessageSender messageSender;
  private final ObjectMapper objectMapper;
  // @Autowired KafkaService kafkaService;

  @Transactional(rollbackFor = Exception.class)
  public ReqRes purchaseAnOrder(long id, String order_payment_id, String paymentType) throws Exception {
    try {
      // Create a new response object to hold the result of the operation
      ReqRes response = new ReqRes();

      // Retrieve the cart for the given user ID
      Cart cart = cartRepo.findByUser(id);

      // Check if the cart is null or empty
      if (cart == null || cart.getCartItems().isEmpty()) {
        // If the cart is null or empty, send an error response indicating the user was not found
        // TODO: Add logic to refund the payment
        response.sendErrorMessage(401, "User not found");
        return response;
      }

      // Retrieve the primary address for the given user ID
      Address address = addressRepo.findByPrimeryAddress(id);

      // Initialize and save the order using the retrieved address, cart, order payment ID, and payment type
      Order savedOrder = initializeOrderAndSaveOrder(address, cart, order_payment_id, paymentType);

      // Save the order items associated with the saved order and cart, using the specified payment type
      saveOrderItems(savedOrder, cart, paymentType);

      // If order processing is successful, send a success response with the saved order
      response.sendSuccessResponse(200, "Successfully order placed", savedOrder);
      return response;
    } catch (Exception e) {
      // If an exception occurs, throw a new exception indicating order processing failed
      throw new Exception("Order processing failed");
    }
  }

  public void getCouponCodeForOrder(CouponCode couponCode) {
    couponCode.setStock(couponCode.getStock() - 1);
    couponCodeRepo.save(couponCode);
  }


  public Order initializeOrderAndSaveOrder(Address address, Cart cart, String order_payment_id, String paymentType) {
    // Create a new order and set its properties
    Order initializeOrder = new Order();

    // Set the address associated with the order
    initializeOrder.setAddressId(address);

    // Set the total amount of the order
    initializeOrder.setTotalAmount(cart.getCartTotal());

    // If the cart has a coupon code, decrement its stock and set the discount amount
    if (cart.getCouponCode() != null) {
      // Decrement the stock of the coupon code
      getCouponCodeForOrder(cart.getCouponCode());

      // Set the coupon code on the order
      initializeOrder.setCouponCode(cart.getCouponCode());

      // Set the discount amount on the order
      initializeOrder.setDiscountAmount(cart.getDiscountCartTotal() - 50);
    } else {
      // If there is no coupon code, set the discount amount to the cart total
      initializeOrder.setDiscountAmount(cart.getDiscountCartTotal());
      initializeOrder.setCouponCode(null);
    }

    // Set the payment ID associated with the order
    initializeOrder.setTransectionId(order_payment_id);

    // Set the tracking ID associated with the order (empty string for now)
    initializeOrder.setTrackingId("");

    // Set the shipping amount associated with the order (50 for now)
    initializeOrder.setShipingAmount(50);

    // Set the user associated with the order
    initializeOrder.setUser(cart.getUser());

    // Set the payment type and status associated with the order
    if (paymentType.equals("NOTPAID")) {
      // If the payment type is "NOTPAID", set the payment type to NOTPAID and the status to PENDING
      initializeOrder.setPaymentType(PaymentType.NOTPAID);
      initializeOrder.setStatus(OrderStatus.PENDING);
    } else {
      // If the payment type is "PAID", set the payment type to PAID and the status to PLACED
      initializeOrder.setPaymentType(PaymentType.PAID);
      initializeOrder.setStatus(OrderStatus.PLACED);
    }

    // Set the expected delivery date associated with the order (10 days from now)
    initializeOrder.setExpectedDeleveryDate(LocalDateTime.now().plusDays(10));

    // Save the order to the database
    Order savedOrder = orderRepo.save(initializeOrder);

    // Return the saved order
    return savedOrder;
  }

  public void saveOrderItems(Order savedOrder, Cart cart, String paymentType) throws Exception {
    // First, get a list of all the cart item IDs
    List<Long> cartItemIds = new ArrayList<>();
    
    // Next, initialize an empty list to hold the order items we will create later
    List<OrderItems> orderItems = new ArrayList<>();
    
    // Next, initialize an empty map to hold the products and their quantities that need to be updated in the products table
    Map<Long , Integer> updateProducts = new HashMap<>();
    
    // Now, loop through the cart items in the cart
    for (CartItems cartProducts : cart.getCartItems()) {
      // Create a new order item
      OrderItems orderProducts = new OrderItems();
      
      // Set the order (saved order) associated with this order item
      orderProducts.setOrder(savedOrder);
      
      // Set the quantity of this order item to the quantity of the cart product
      orderProducts.setQuantity(cartProducts.getQuantity());
      
      // Set the products associated with this order item to the product associated with the cart product
      orderProducts.setProducts(cartProducts.getProduct());
      
      // Set the total price of this order item to the quantity of the cart product times the price of the product
      orderProducts.setTotalprice(cartProducts.getProduct().getPrice() * cartProducts.getQuantity());
      
      // Set the color of this order item to the color of the product
      orderProducts.setColor(cartProducts.getProduct().getColor());
      
      // Add the ID of the cart item to the list of cart item IDs
      cartItemIds.add(cartProducts.getId());
      
      // Add the order item to the list of order items
      orderItems.add(orderProducts);
      
      // Add the product and quantity to the map of products to update
      updateProducts.put(cartProducts.getProduct().getId(), cartProducts.getQuantity());
      
      // If the payment type is PAID, then update the stock of the product in the products table
      if (paymentType.equals("PAID")) {
        cartProducts.getProduct().setStock(cartProducts.getProduct().getStock() - cartProducts.getQuantity());
      }
    }
    
    // Send a message to the queue with the list of products and their quantities to update
    messageSender.sendMessageToQueue(RabbitMqConfig.UPDATE_PRODUCT_QUEUE, objectMapper.writeValueAsString(updateProducts));
    
    // Save all the order items to the order items table
    orderItemsRepo.saveAll(orderItems);
    
    // If the payment type is PAID, then remove all the cart items from the cart
    if (paymentType.equals("PAID")) {
      cartUtilService.RemoveCartProducts(cart, cartItemIds);
    }
  }

  


  //// >>>>>>>>>>>>>>>>>>>Order Service Ends<<<<<<<<<<<<<<<<<<<<<<<<<<<<

 
  public boolean isOrderAlreadyThere(String id) {
    // Try to find an order with the given transection ID in the ORDER table
    Order foundPendingOrder = orderRepo.findByTransectionId(id).orElse(null);

    // If the order is not found, return false
    if (foundPendingOrder == null) {
      return false;
    }

    // If the order is found, update its payment type and status
    foundPendingOrder.setPaymentType(PaymentType.PAID);
    foundPendingOrder.setStatus(OrderStatus.PLACED);

    // Save the updated order to the ORDER table
    orderRepo.save(foundPendingOrder);

    // Return true to indicate that the order was updated successfully
    return true;
  }

  
  public int getFailedForSchedulerToCheck(int page, int size) {

    // Sort the orders in descending order by created at date
    Sort sort = Sort.by(Sort.Direction.DESC, "created_at");
    // Set the page and size based on the input parameters
    Pageable pageable = PageRequest.of(page, size, sort);

    // Find all the orders that are in the PENDING status
    Page<Order> pageValues = orderRepo.findPendingOrders(pageable);
    // Get the list of orders from the page
    List<Order> orders = pageValues.getContent();

    // Create a list to hold the products that we need to update
    List<Products> updateAbleProducts = new ArrayList<>();
    // Create a list to hold the orders that we need to update
    List<Order> updateAbleOrders = new ArrayList<>();

    // Loop through the orders
    for (Order order : orders) {
      // Update the payment type and status of the order
      order.setPaymentType(PaymentType.PAID);
      order.setStatus(OrderStatus.PLACED);
      // Set the expected delivery date to be 10 days from now
      order.setExpectedDeleveryDate(LocalDateTime.now().plusDays(10));
      // Get the list of order items associated with the order
      List<OrderItems> orderItems = orderItemsRepo.findByOrderId(order.getId());
      // Loop through the order items
      orderItems.stream().map(e -> {
        // Update the quantity of the order item
        e.setQuantity(e.getQuantity());
        // Add the product to the list of products that need to be updated
        updateAbleProducts.add(e.getProducts());
        return null;
      });
      // Add the order to the list of orders that need to be updated
      updateAbleOrders.add(order);
    }

    // Save the updated orders to the database
    orderRepo.saveAll(updateAbleOrders);
    // Save the updated products to the database
    productRepo.saveAll(updateAbleProducts);

    // Return the total number of pages
    return pageValues.getTotalPages();
  }


  public boolean successTheFailedOrdersInScheduler() {
    try {
      // Get the total number of pages of failed orders
      int page = 0;
      int size = 15;
      int pages = getFailedForSchedulerToCheck(page, size);
      
      // Loop through each page of failed orders
      page += 1;
      if (pages > 0) {
        for (int i = 0; i < pages; i++) {
          // Call the getFailedForSchedulerToCheck method to update the orders
          // on this page
          getFailedForSchedulerToCheck(page, size);
          // Increment the page number
          page++;
        }
      }
      // If we get to this point, the method was successful
      return true;
    } catch (Exception e) {
      // If there is an exception, return false
      return false;
    }
  }

  public boolean deleteOrder(Order order) {
    try {
      orderRepo.delete(order);
      return true;
    } catch (Exception e) {
      return false;
    }

  }

  public void deleteExpiredOrders() {

  }

  /// It will handle The failed orders
  ///
  /// This method takes a Map of String keys and values
  /// and uses it to create a JSONObject that contains
  /// the razorpay_order_id, the user ID, and the paymentType
  /// (which is set to NOTPAID)
  ///
  /// The resulting JSONObject is then sent to the Kafka topic
  /// "order_topic" using the kafkaService.
  ///
  /// The method returns a ReqRes object that contains a success
  /// message indicating that the failed transection was saved
  /// and will be confirmed within 30 minutes.
  ///
  public ReqRes handleFailedOrders(Map<String, String> value) {
    // Create a JSONObject from the Map
    JSONObject jsonObject = new JSONObject();
    // Set the razorpay_order_id from the Map
    jsonObject.put("razorpay_order_id", value.get("razorpay_order_id"));
    // Set the user ID to 1 (hardcoded for now)
    jsonObject.put("user", 1);
    // Set the paymentType to NOTPAID
    jsonObject.put("paymentType", PaymentType.NOTPAID);
    // Send the JSONObject to the Kafka topic
    // kafkaService.sendMessage("order_topic", jsonObject);
    // Create a ReqRes object to return
    ReqRes response = new ReqRes();
    // Set the success message
    response.sendSuccessResponse(200,
        "Successfully saved the failed transection and it will be conform within 30 minutes");
    // Return the ReqRes object
    return response;
  }

  // Show all the orders to the user
  //
  // This method takes an HttpServletRequest object as a parameter
  // and returns a ReqRes object.
  //
  // The method first creates a ReqRes object and stores it in the
  // "response" variable.
  //
  // It then calls the getIdFromJwt() method from the jwtInterceptor
  // to get the ID of the user making the request from the JWT
  // in the request.
  //
  // It then uses the ID of the user to find all the orders for
  // that user by calling the findOrders() method from the orderRepo.
  //
  // If the orders are null or the list is empty, it sets an error
  // message in the response with a 404 status code.
  //
  // If the orders are not null and the list is not empty, it sets
  // the orders in the response with a 200 status code.
  //
  public ReqRes getAlltheOrders(HttpServletRequest httpServletRequest) throws Exception {
    ReqRes response = new ReqRes();
    long user = jwtInterceptor.getIdFromJwt(httpServletRequest);

    List<OrderDetailsSendDTO> orders = orderRepo.findOrders(user);
    if (orders == null || orders.isEmpty()) {
      response.sendErrorMessage(404, "No Order found with the current user ");
      return response;
    }

    response.sendSuccessResponse(200, "Success", orders);
    return response;
  }

}
