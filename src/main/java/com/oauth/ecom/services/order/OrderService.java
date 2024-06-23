package com.oauth.ecom.services.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oauth.ecom.dto.order.OrderProcessTransectionDetails;
import com.oauth.ecom.entity.*;
import com.oauth.ecom.entity.enumentity.PaymentType;
import com.oauth.ecom.repository.*;
import com.oauth.ecom.util.JwtInterceptor;
import com.oauth.ecom.util.ReqRes;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class OrderService {
  

  

  private static final PaymentType PAID =PaymentType.PAID ;
  @Autowired
  private JwtInterceptor jwtInterceptor;
  @Autowired
  private ProductRepo productRepo;
  @Autowired
  private CartRepo cartRepo;
  @Autowired
  private CartItemsRepo cartItemsRepo;
  @Autowired
  private UserRepo userRepo;
  @Autowired
  private OrderRepo orderRepo;
  @Autowired 
  private OrderItemsRepo orderItemsRepo;
  @Autowired
  private AddressRepo addressRepo;

  @Autowired
  private CouponCodeRepo  couponCodeRepo;
  @Transactional(rollbackFor=Exception.class) // it will roll back if anything goes wrong
  public ReqRes makeAOrder(HttpServletRequest httpServletRequest , String transectionId) throws Exception{
    ReqRes response = new ReqRes();
    
    Long user = (long) jwtInterceptor.getIdFromJwt(httpServletRequest); // get the user from the session
    Cart cartInfo = cartRepo.findByUser(user);
    if (cartInfo == null || cartInfo.getCartItems() == null || cartInfo.getCartItems().isEmpty() || cartInfo.getCartItems().size() == 0) {
        response.setMessage("Producs not there");
        response.setStatusCode(400);
        return response;
    }
    Order savedOrder  = initializeOrder(user, transectionId);
    float TotalPrice  = OrderProcessingOperation(cartInfo, savedOrder);
    SaveOrderDetails(savedOrder, TotalPrice);
    response.setMessage("Order placed successfully done!");
    response.setStatusCode(200);
    response.setIsSuccess(true);
    return response;
  }

  public Order initializeOrder(Long user , String transectionId ){
    Optional<UserEntity> foundUser = userRepo.findById(user);
    Address address = addressRepo.findByUser(user);
    Order order = new Order();
    order.setUser(foundUser.get());
    order.setTransectionId(transectionId);
    order.setAddressId(address);
    Order savedOrder = orderRepo.save(order);
    return savedOrder;
  }
  public float OrderProcessingOperation(Cart cartInfo ,Order savedOrder) throws Exception{
    float TotalPrice = 0;
    for (CartItems e : cartInfo.getCartItems()) {
      TotalPrice +=  CheckProductParamiters(e, TotalPrice, savedOrder, cartInfo);
      }
      return TotalPrice;
  }
  public float CheckProductParamiters(CartItems e , float TotalPrice , Order savedOrder , Cart cartInfo) throws Exception{
    Products cartProduct = e.getProduct();
    Products foundProduct =   productRepo.findByProductId(cartProduct.getId());
    // if (foundProduct.getStock() - e.getQuantity() <= 0) throw new Exception("Order not placed");
    // if i check the quantity than it will lead us a big problem
    // because this operation runs only when the user pays money 
    // that's i have to accept the order and process the order 
    foundProduct.setStock(foundProduct.getStock() - e.getQuantity());
    OrderItems orderItem = new OrderItems();
    orderItem.setColor(cartProduct.getColor());
    orderItem.setQuantity(e.getQuantity());
    orderItem.setTotalprice(e.getQuantity() * cartProduct.getPrice());
    orderItem.setProducts(foundProduct);
    orderItem.setOrder(savedOrder);
    TotalPrice+=e.getQuantity() * cartProduct.getPrice();
    cartItemsRepo.deleteCartItems(cartInfo.getId());
    cartInfo.setCartItems(null);
    cartRepo.save(cartInfo);
    orderItemsRepo.save(orderItem);
    return TotalPrice;
  }
  public void SaveOrderDetails(Order savedOrder , float TotalPrice){
    savedOrder.setDiscountAmount(0);
    savedOrder.setNetAmount(TotalPrice);
    savedOrder.setPaymentType(PAID);
    LocalDateTime DeleveryDate =LocalDateTime.of( 2024 , 12 , 20  , 7 , 20 , 20);
    savedOrder.setDeleveryDate(DeleveryDate);
    savedOrder.setNetAmount(TotalPrice - 50);
    savedOrder.setShipingAmount(50);
    
    orderRepo.save(savedOrder);
  }









  @Transactional
  
 public ReqRes purchaseAnOrder(HttpServletRequest request ,OrderProcessTransectionDetails orderProcessTransectionDetails ) throws Exception{
  try {
    ReqRes response = new ReqRes();
    
  long id =(long) jwtInterceptor.getIdFromJwt(request);
 Cart cart =  cartRepo.findByUser(id);
 if (cart == null || cart.getCartItems().isEmpty()) {
  response.sendErrorMessage(401, "User not found");
  return response;
 }
 Address address = addressRepo.findByPrimeryAddress(id);
 Order savedOrder = initializeOrderAndSaveOrder(address , cart , orderProcessTransectionDetails);
saveOrderItems( savedOrder , cart);
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
public Order initializeOrderAndSaveOrder(Address address , Cart cart , OrderProcessTransectionDetails orderProcessTransectionDetails){
  Order initializeOrder = new Order();
 initializeOrder.setAddressId(address);
 initializeOrder.setNetAmount(cart.getCartTotal());
 if (cart.getCouponCode() == null||cart.getCouponCode().equals(null) ) {
    initializeOrder.setCouponCode(null);
    initializeOrder.setDiscountAmount(cart.getDiscountCartTotal());
 }
 else{
 getCouponCodeForOrder(cart.getCouponCode());
initializeOrder.setCouponCode(cart.getCouponCode());
initializeOrder.setDiscountAmount(cart.getDiscountCartTotal());
 }
 initializeOrder.setTrackingId(orderProcessTransectionDetails.getTransectionId());
initializeOrder.setShipingAmount(50);
initializeOrder.setUser(cart.getUser());
Order savedOrder = orderRepo.save(initializeOrder);
return savedOrder;
}
public void saveOrderItems(Order savedOrder , Cart cart) throws Exception{
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
  cartProducts.getProduct().setStock(cartProducts.getProduct().getStock() - cartProducts.getQuantity());
  updateProducts.add(cartProducts.getProduct());
}
UpdateProductQuantity(updateProducts);
orderItemsRepo.saveAll(orderItems);
RemoveCartProducts(cart , cartItemIds);
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
}


// get the cart info
// get the total price and also check the coupon code is valid or not 
// if valid than get the discount to the user 
// after that get the discounted total and save the order and the following details 





