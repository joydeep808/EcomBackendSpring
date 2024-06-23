package com.oauth.ecom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.oauth.ecom.dto.cart.AddCartQuantityDto;
import com.oauth.ecom.services.cart.CartService;
import com.oauth.ecom.util.ReqRes;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
  
  @Autowired
  private CartService cartService;


  @PostMapping("/add")
  public ResponseEntity<ReqRes> addProducts(HttpServletRequest httpServletRequest , @RequestBody AddCartQuantityDto cartItemsDto) {
    ReqRes response = cartService.addProductInCart(httpServletRequest ,cartItemsDto);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
  @GetMapping("/get")
  public ResponseEntity<ReqRes> getCartProducts(HttpServletRequest httpServletRequest){
    ReqRes response = cartService.getCartInfo(httpServletRequest);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
  @DeleteMapping("/delete")
  public ResponseEntity<ReqRes> deleteCartItems(HttpServletRequest httpServletRequest) throws Exception{
    ReqRes response = cartService.removeProductFromCart(httpServletRequest);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
}
