package com.oauth.jwtauth.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oauth.jwtauth.dto.ReqRes;
import com.oauth.jwtauth.services.order.OrderService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
  @Autowired
  private OrderService orderService;
  @PostMapping("/buy")

  public ResponseEntity<ReqRes> createOrder(HttpServletRequest  httpServletRequest , @RequestBody Map<String , String> transectionId){
    try {
    return ResponseEntity.ok(orderService.makeAOrder(httpServletRequest, transectionId.get("transectionId")));
      
    } catch (Exception e) {
      ReqRes res = new ReqRes();
      res.setMessage(e.getMessage());
      res.setStatusCode(500);

      return ResponseEntity.ok(res);
    }
  }
}
