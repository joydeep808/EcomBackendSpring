package com.oauth.ecom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.oauth.ecom.dto.cart.AddCartQuantityDto;
import com.oauth.ecom.entity.CartItems;
import com.oauth.ecom.services.cart.CartService;
import com.oauth.ecom.util.ReqRes;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

  @Autowired
  private CartService cartService;

  @PostMapping("/add")
  public ResponseEntity<ReqRes<Object>> addProducts(HttpServletRequest httpServletRequest,
      @RequestBody AddCartQuantityDto cartItemsDto) throws Exception {
    return cartService.addProductInCart(httpServletRequest, cartItemsDto);
  }

  @GetMapping("/get")
  public ResponseEntity<ReqRes<List<CartItems>>> getCartProducts(HttpServletRequest httpServletRequest)
      throws Exception {
    return cartService.getCartInfo(httpServletRequest);
  }

  @DeleteMapping("/delete")
  public ResponseEntity<ReqRes<Object>> deleteCartItems(HttpServletRequest httpServletRequest) throws Exception {
    return cartService.removeProductFromCart(httpServletRequest);
  }
}
