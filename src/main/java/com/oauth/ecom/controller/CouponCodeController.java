package com.oauth.ecom.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.oauth.ecom.entity.Cart;
import com.oauth.ecom.entity.CouponCode;
import com.oauth.ecom.services.couponcode.CouponCodeService;
import com.oauth.ecom.util.ReqRes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/couponCode")
public class CouponCodeController {
  @Autowired
  private CouponCodeService couponCodeService;

  @PostMapping("/create")
  public ResponseEntity<ReqRes<CouponCode>> createCouponCode(@RequestBody @Valid CouponCode couponCode){
      return couponCodeService.createCoupon(couponCode);
  }
  @PostMapping("/add")
  public ResponseEntity<ReqRes<Cart>> addCouponCode(HttpServletRequest request ,@RequestBody  Map<String , String> couponCode) throws Exception{
      return couponCodeService.addCouponInCart(request, couponCode.get("couponCode"));
  }
  @GetMapping("/remove")
  public ResponseEntity<ReqRes<Cart>> removeCouponCode(HttpServletRequest request) throws Exception{
      return couponCodeService.removeCouponCode(request);
  }
}
