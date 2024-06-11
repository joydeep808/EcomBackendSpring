package com.oauth.jwtauth.services.cart;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oauth.jwtauth.dto.ReqRes;
import com.oauth.jwtauth.dto.cartitems.CreateCartItemsDto;
import com.oauth.jwtauth.entity.Cart;
import com.oauth.jwtauth.entity.CartItems;
import com.oauth.jwtauth.entity.Products;
import com.oauth.jwtauth.repository.CartItemsRepo;
import com.oauth.jwtauth.repository.CartRepo;
import com.oauth.jwtauth.repository.ProductRepo;
import com.oauth.jwtauth.util.JwtInterceptor;

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
  public ReqRes addProductInCart(HttpServletRequest httpServletRequest  , CreateCartItemsDto cartItemsDto) {
    ReqRes response = new ReqRes();
    try {
     Optional<Products> product =productRepo.findById(cartItemsDto.getProductId());

     if (product == null || product.isEmpty()) {
       response.setMessage("Product not found");
       response.setStatusCode(404);;
       return response;
     }
     if (product.get().getStock() <= 0) {
      response.setMessage("Product is out stock");
      response.setStatusCode(400);
      return response;
     }
     Long user =  (long) jwtInterceptor.getIdFromJwt(httpServletRequest);
     if (user.equals(null)) {
      response.setMessage("Please Login");
      response.setStatusCode(401);
      return response;
     }
     Cart cart = cartRepo.findByUser(user);
     boolean isAlreadyThere  = cart.getCartItems().stream().filter(p-> p.getProduct().getId() == cartItemsDto.getProductId()).findFirst().map(p-> {p.setQuantity(p.getQuantity() + 1);
     return true;}).orElse(false);
     if (isAlreadyThere) {
       cartRepo.save(cart);
       response.setMessage("Update successfully done");;
       response.setStatusCode(200);
       response.setIsSuccess(true);
       return response;
     }
     CartItems cartItems = new CartItems();
     cartItems.setProduct(product.get());
     cartItems.setCart(cart);
     cartItems.setQuantity(cartItemsDto.getQuantity());
     cartItemsRepo.save(cartItems);
     response.setMessage("New Product added successfully!");
     response.setIsSuccess(true);
     response.setStatusCode(200);
     return response;
   } catch (Exception e) {
    response.setError(e.getLocalizedMessage());
    response.setMessage(e.getMessage());
    response.setStatusCode(500);
    return response;
   }
  }



  public ReqRes removeProductFromCart(HttpServletRequest httpServletRequest) throws Exception{
    ReqRes response = new ReqRes();
    Long id = (long) jwtInterceptor.getIdFromJwt(httpServletRequest);
   Cart cart=  cartRepo.findByUser(id);
   cartItemsRepo.deleteCartItems(cart.getId());
   response.setMessage("Success ");
   response.setIsSuccess(true);
   response.setStatusCode(200);
   return response;

  }
}