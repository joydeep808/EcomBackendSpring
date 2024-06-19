package com.oauth.ecom.services.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oauth.ecom.dto.ReqRes;
import com.oauth.ecom.dto.cart.AddCartQuantityDto;
import com.oauth.ecom.dto.cart.DecrementCartQuantityDto;
import com.oauth.ecom.dto.product.ProductParseDto;
import com.oauth.ecom.entity.*;
import com.oauth.ecom.repository.*;
import com.oauth.ecom.services.kafka.KafkaService;
import com.oauth.ecom.services.redis.RedisService;
import com.oauth.ecom.util.JwtInterceptor;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CartService {
  @Autowired
  private JwtInterceptor jwtInterceptor;
  @Autowired
  private CartRepo cartRepo;
  @Autowired
  private ProductRepo productRepo;
  @Autowired 
  private CartItemsRepo cartItemsRepo;
  @Autowired
  private RedisService redisService;
  @Autowired(required = true)
  private KafkaService kafkaService;
 

  public ReqRes addProductInCart(HttpServletRequest httpServletRequest  , AddCartQuantityDto cartItemsDto) {
    ReqRes response = new ReqRes();
    try {
     Products product =productRepo.findById(cartItemsDto.getProductId()).orElse(null);

     if (product == null  ) {
      response.sendErrorMessage(404,"Product not found" );
       return response;
     }
     if (product.getStock() <= 0 ) {
      response.sendErrorMessage(400, "Product is out of stock");
      return response;
     }
     if (product.getStock() - cartItemsDto.getQuantity() < 0) {
      response.sendErrorMessage(400,"Please try again with an lower stock" );
      return response;
     }
      Long user =  (long) jwtInterceptor.getIdFromJwt(httpServletRequest);
     ProductParseDto productDetails = new ProductParseDto(product.getId() , user , cartItemsDto.getQuantity());
     kafkaService.sendMessage("cart_topic" , productDetails ); 
     response.sendSuccessResponse(200, "Cart will be updated shortly");
     return response;
     // i will send the product id or product itself 
     // after that in the kafka consumer the cart logic is processed 

    //  if (user.equals(null)) {
    //   response.setMessage("Please Login");
    //   response.setStatusCode(401);
    //   return response;
    //  }
    //  Cart cart = cartRepo.findByUserId(user);
    //  boolean isAlreadyThere  = cart.getCartItems().stream().filter(p-> p.getProduct().getId() == cartItemsDto.getProductId()).findFirst().map(p-> {p.setQuantity(p.getQuantity() + cartItemsDto.getQuantity());
    //  return true;}).orElse(false);
    //  if (isAlreadyThere) {
    //    cartRepo.save(cart);
    //    response.setMessage("Update successfully done");;
    //    response.setStatusCode(200);
    //    response.setIsSuccess(true);
    //    return response;
    //  }
    //  CartItems cartItems = new CartItems();
    //  cartItems.setProduct(product.get());
    //  cartItems.setCart(cart);
    //  cartItems.setQuantity(cartItemsDto.getQuantity());
    //  cartItemsRepo.save(cartItems);
    //  response.setMessage("New Product added successfully!");
    //  response.setIsSuccess(true);
    //  response.setStatusCode(200);
    //  return response;
   } catch (Exception e) {
    response.sendErrorMessage(500 , e.getMessage());
    return response;
   }
  }
  @Transactional(rollbackFor=Exception.class)
  public ReqRes removeProductFromCart(HttpServletRequest httpServletRequest) throws Exception{
    ReqRes response = new ReqRes();
    try {
      Long id = (long) jwtInterceptor.getIdFromJwt(httpServletRequest);
     Cart cart=  cartRepo.findByUser(id);
     if (cart.getCartItems().size() == 0) {
      throw new Exception("No Products found to remove");
     }
     cartItemsRepo.deleteCartItems(cart.getId());
      if (cart.getCartItems().size() == 1) {
        cart.setCartTotal(0);
      }
      else{
        int totalPrice = 0;
        for (CartItems cartItem : cart.getCartItems()) {
        totalPrice+=(cartItem.getProduct().getPrice() * cartItem.getQuantity());
      }
        cart.setCartTotal(totalPrice);
      }
      cartRepo.save(cart);
      response.sendSuccessResponse(200, "Success fully removed");
     return response;
  
    } catch (Exception e) {
      throw new Exception(e.getMessage());
    }
  }
  @Transactional
  public ReqRes decrementCartQuantity(HttpServletRequest httpServletRequest ,DecrementCartQuantityDto cartQuantityDto ) throws Exception{
    ReqRes response = new ReqRes();
    try {
    long userId =  jwtInterceptor.getIdFromJwt(httpServletRequest);
    Cart cart = cartRepo.findByUser(userId);
    if (cart == null || cart.getCartItems().isEmpty()) {
      response.sendErrorMessage(401, "We are having some issue");
      return response;
    }
   boolean isCartUpdated =  cart.getCartItems().stream().filter(pro-> pro.getProduct().getId() == cartQuantityDto.getProductId()).findFirst().map(p->{
        p.setQuantity(p.getQuantity() - 1);
        cart.setCartTotal(cart.getCartTotal() - p.getProduct().getPrice());
        cartRepo.save(cart);
        return true;
    }).orElse(false);
    if (isCartUpdated) {
      response.sendSuccessResponse(200, "Quantity decrement successfully done!" , cart);
      return response;
    }
    response.sendErrorMessage(404, "Product not found to decrement the quantity");
    return response;

    } catch (Exception e) {
    response.sendErrorMessage(500, e.getMessage());
     throw new Exception(e.getMessage());
    }

  }

  public ReqRes getCartInfo(HttpServletRequest httpServletRequest){
    ReqRes response = new ReqRes();
    try {
     Long id   = (long) jwtInterceptor.getIdFromJwt(httpServletRequest);
    Object CacheCartInfo =  redisService.getData("CART"+id, Object.class);
    if (CacheCartInfo != null) {
      response.sendSuccessResponse(200 , "Cart Products found from redis" , CacheCartInfo);
      return response;
    }
    Cart cartinfo =  cartRepo.findByUser(id);
    if (cartinfo == null || cartinfo.getCartItems().isEmpty() || cartinfo.getCartItems().size() == 0) {
      response.setMessage("Empty Cart");
      response.setStatusCode(400);
      return response;
    }
    redisService.saveInSingleQuery("CART"+id, cartinfo.getCartItems(), 20);
    response.sendSuccessResponse(200 , "Cart Products found from redis" , cartinfo.getCartItems());
    return response;

    } catch (Exception e) {
      response.sendErrorMessage(500, e.getMessage());
      response.setStatusCode(500);
      return response;
    }
  }

}