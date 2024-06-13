package com.oauth.jwtauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oauth.jwtauth.dto.ReqRes;
import com.oauth.jwtauth.dto.cartitems.CreateCartItemsDto;
import com.oauth.jwtauth.services.cart.CartService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
  
  @Autowired
  private CartService cartService;


  @PostMapping("/add")
  public ResponseEntity<ReqRes> addProducts(HttpServletRequest httpServletRequest , @RequestBody CreateCartItemsDto cartItemsDto) throws Exception{
    return ResponseEntity.ok(cartService.addProductInCart(httpServletRequest ,cartItemsDto));
  }
  @GetMapping("/get")
  public ResponseEntity<ReqRes> getCartProducts(HttpServletRequest httpServletRequest){
    return ResponseEntity.ok(cartService.getCartInfo(httpServletRequest));
  }
  @DeleteMapping("/delete")
  public ResponseEntity<ReqRes> deleteCartItems(HttpServletRequest httpServletRequest) throws Exception{
    return ResponseEntity.ok(cartService.removeProductFromCart(httpServletRequest));
  }
}
