package com.oauth.ecom.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.oauth.ecom.dto.ReqRes;
import com.oauth.ecom.entity.CouponCode;
import com.oauth.ecom.services.couponCode.CouponCodeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/couponCode")
public class CouponCodeController {
  @Autowired
  private CouponCodeService couponCodeService;

  @PostMapping("/create")
  public ResponseEntity<ReqRes> createCouponCode(@RequestBody @Valid CouponCode couponCode){
    ReqRes response = couponCodeService.createCoupon(couponCode);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
  @PostMapping("/add")
  public ResponseEntity<ReqRes> addCouponCode(HttpServletRequest request ,@RequestBody  Map<String , String> couponCode){
    ReqRes response = couponCodeService.addCouponInCart(request, couponCode.get("couponCode"));
  return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
  @GetMapping("/remove")
  public ResponseEntity<ReqRes> removeCouponCode(HttpServletRequest request){
    ReqRes response = couponCodeService.removeCouponCode(request);
    return  response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
}
