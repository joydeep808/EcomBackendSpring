package com.oauth.jwtauth.services.order;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oauth.jwtauth.dto.ReqRes;
import com.oauth.jwtauth.entity.*;
import com.oauth.jwtauth.entity.enumentity.PaymentType;
import com.oauth.jwtauth.repository.*;
import com.oauth.jwtauth.util.JwtInterceptor;

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

  @Transactional(rollbackFor=Exception.class)
  public ReqRes makeAOrder(HttpServletRequest httpServletRequest , String transectionId) throws Exception{
    ReqRes response = new ReqRes();
    Long user = (long) jwtInterceptor.getIdFromJwt(httpServletRequest);
    Cart cartInfo = cartRepo.findByUser(user);
    if ( cartInfo.getCartItems() == null || cartInfo.getCartItems().isEmpty() || cartInfo.getCartItems().size() == 0) {
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
    if (foundProduct.getStock() - e.getQuantity() <= 0) throw new Exception("Order not placed");
    
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



}
