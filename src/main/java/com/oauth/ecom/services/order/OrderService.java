package com.oauth.ecom.services.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oauth.ecom.dto.order.OrderDetailsSendDTO;
import com.oauth.ecom.entity.*;
import com.oauth.ecom.entity.enumentity.OrderStatus;
import com.oauth.ecom.entity.enumentity.PaymentType;
import com.oauth.ecom.repository.*;
import com.oauth.ecom.services.kafka.KafkaService;
import com.oauth.ecom.util.JwtInterceptor;
import com.oauth.ecom.util.ReqRes;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class OrderService {
  

  @Autowired JwtInterceptor jwtInterceptor;
  @Autowired ProductRepo productRepo;
  @Autowired CartRepo cartRepo;
  @Autowired CartItemsRepo cartItemsRepo;
  @Autowired UserRepo userRepo;
  @Autowired OrderRepo orderRepo;
  @Autowired  OrderItemsRepo orderItemsRepo;
  @Autowired AddressRepo addressRepo;
  @Autowired CouponCodeRepo  couponCodeRepo;
  @Autowired KafkaService kafkaService;




  @Transactional(rollbackFor = Exception.class)
 public ReqRes purchaseAnOrder(long id ,String order_payment_id  , String paymentType) throws Exception{
  try {
ReqRes response = new ReqRes();
 Cart cart =  cartRepo.findByUser(id);
 if (cart == null || cart.getCartItems().isEmpty()) {
  response.sendErrorMessage(401, "User not found");
  return response;
 }
 Address address = addressRepo.findByPrimeryAddress(id);
 Order savedOrder = initializeOrderAndSaveOrder(address , cart , order_payment_id , paymentType);
saveOrderItems( savedOrder , cart , paymentType);
 response.sendSuccessResponse(200, "Successfully order placed" , savedOrder);
 return response;
  } catch (Exception e) {
   throw new Exception("Order processing failed");
  }

}
public void getCouponCodeForOrder(CouponCode couponCode){
couponCode.setStock(couponCode.getStock() - 1);
couponCodeRepo.save(couponCode);
}
public Order initializeOrderAndSaveOrder(Address address , Cart cart , String order_payment_id , String paymentType){
  Order initializeOrder = new Order();
 initializeOrder.setAddressId(address);
 initializeOrder.setTotalAmount(cart.getCartTotal());
 if (cart.getCouponCode() == null) {
    initializeOrder.setCouponCode(null);
    initializeOrder.setDiscountAmount(cart.getDiscountCartTotal());
 }
 else{
getCouponCodeForOrder(cart.getCouponCode());
initializeOrder.setCouponCode(cart.getCouponCode());
initializeOrder.setDiscountAmount(cart.getDiscountCartTotal() - 50);
}
 initializeOrder.setTransectionId(order_payment_id);
 initializeOrder.setTrackingId("");
initializeOrder.setShipingAmount(50);
initializeOrder.setUser(cart.getUser());
if (paymentType.equals("NOTPAID")) {
  initializeOrder.setPaymentType(PaymentType.NOTPAID);
  initializeOrder.setStatus(OrderStatus.PENDING);
}
else{
  initializeOrder.setPaymentType(PaymentType.PAID);
  initializeOrder.setStatus(OrderStatus.PLACED);
}
initializeOrder.setExpectedDeleveryDate(LocalDateTime.now().plusDays(10));
Order savedOrder = orderRepo.save(initializeOrder);
return savedOrder;
}
public void saveOrderItems(Order savedOrder , Cart cart , String paymentType) throws Exception{
  List<Long>  cartItemIds = new ArrayList<>();
  List<OrderItems> orderItems = new ArrayList<>();
  List<Products> updateProducts = new ArrayList<>();
  for (CartItems cartProducts: cart.getCartItems()) {
  OrderItems orderProducts = new OrderItems();
  orderProducts.setOrder(savedOrder);
  orderProducts.setQuantity(cartProducts.getQuantity());
  orderProducts.setProducts(cartProducts.getProduct());
  orderProducts.setTotalprice(cartProducts.getProduct().getPrice() * cartProducts.getQuantity());
  orderProducts.setColor(cartProducts.getProduct().getColor());
  cartItemIds.add(cartProducts.getId());
  orderItems.add(orderProducts);
  updateProducts.add(cartProducts.getProduct());
  if (paymentType.equals("PAID")) {
  cartProducts.getProduct().setStock(cartProducts.getProduct().getStock() - cartProducts.getQuantity());
  }
}
UpdateProductQuantity(updateProducts); /// It will updated
orderItemsRepo.saveAll(orderItems);
if (paymentType.equals("PAID")) {
  RemoveCartProducts(cart , cartItemIds);
}
}
public void UpdateProductQuantity(List<Products> products){
  productRepo.saveAll(products);
}


public void RemoveCartProducts(Cart cart , List<Long> cartItemsId){
  
  cart.setCartTotal(0);
  cart.setCouponCode(null);
  cart.setDiscountCartTotal(0);
  cartRepo.save(cart);  
  cartItemsRepo.deleteCartItems(cart.getId());
  
} 
//// >>>>>>>>>>>>>>>>>>>Order Service Ends<<<<<<<<<<<<<<<<<<<<<<<<<<<<











public boolean isOrderAlreadyThere (String id){
Order foundPendingOrder = orderRepo.findByTransectionId(id).orElse(null);
if (foundPendingOrder == null) {
  return false;
}
foundPendingOrder.setPaymentType(PaymentType.PAID);
foundPendingOrder.setStatus(OrderStatus.PLACED);
orderRepo.save(foundPendingOrder);
return true;
}


public int  performTheOperation(int page , int size){

  Sort sort  = Sort.by(Sort.Direction.DESC , "created_at");
  Pageable pageable = PageRequest.of(page, size  , sort);
 Page<Order> pageValues =  orderRepo.findPendingOrders(pageable); /// 

 List<Order> orders = pageValues.getContent(); /// the orders that are found in the query
  List<Products> updateAbleProducts = new ArrayList<>();
  List<Order> updateAbleOrders = new ArrayList<>();
  for (Order order : orders) {
  order.setPaymentType(PaymentType.PAID);
  order.setStatus(OrderStatus.PLACED);
  order.setExpectedDeleveryDate(LocalDateTime.now().plusDays(10));
  List<OrderItems> orderItems = orderItemsRepo.findByOrderId(order.getId());
  orderItems.stream().map(e->{
    e.setQuantity(e.getQuantity());
    updateAbleProducts.add(e.getProducts());
    return null;
  });
  updateAbleOrders.add(order);
  }
  orderRepo.saveAll(updateAbleOrders);
  productRepo.saveAll(updateAbleProducts);
  return pageValues.getTotalPages();
}

public boolean successTheFailedOrders(){
try {
  int page = 0;
  int size = 15;
  int pages = performTheOperation(page, size);
  page+=1;
  if (pages > 0) {
   for (int i = 0; i < pages; i++) {
    performTheOperation(page, size);
    page++;
   }
  }
  return true;
} catch (Exception e) {
  return false;
}
}

public boolean deleteOrder(Order order){
 try {
   orderRepo.delete(order);
   return true;
 } catch (Exception e) {
  return false;
 }

}

public void deleteExpiredOrders(){
  
}



 /// It will handle The failed orders 
public ReqRes handleFailedOrders(Map<String , String> value){
  JSONObject jsonObject = new JSONObject();
  jsonObject.put("razorpay_order_id", value.get("razorpay_order_id"));
  jsonObject.put("user", 1);
  jsonObject.put("paymentType", PaymentType.NOTPAID);
  kafkaService.sendMessage("order_topic", jsonObject);
  ReqRes response = new ReqRes();
  response.sendSuccessResponse(200, "Successfully saved the failed transection and it will be conform within 30 minutes");
  return response;

}



// Show all the orders to the user
public ReqRes getAlltheOrders(HttpServletRequest httpServletRequest) throws Exception{
  ReqRes response = new ReqRes();
  long user = jwtInterceptor.getIdFromJwt(httpServletRequest);

  List<OrderDetailsSendDTO> orders = orderRepo.findOrders(user);
  if (orders == null || orders.isEmpty()) {
    response.sendErrorMessage(404, "No Order found with the current user ");
    return response;
  }
  
  response.sendSuccessResponse(200, "Success" , orders);
  return response;
}

}
