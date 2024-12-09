package com.oauth.ecom.controller;

import java.util.*;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.oauth.ecom.dto.order.*;
import com.oauth.ecom.entity.enumentity.PaymentType;
import com.oauth.ecom.services.order.*;
import com.oauth.ecom.services.payment.PaymentService;
import com.oauth.ecom.util.ReqRes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
  private final PaymentService paymentService;
  private final OrderService orderService;
  private final PaymentCallbackProcessor paymentCallbackProcessor;
  // @Autowired KafkaService kafkaService;
  @GetMapping(path = "/request")
  public ResponseEntity<ReqRes<PaymentRequestDto>> paymentRequest(HttpServletRequest httpServletRequest) throws Exception {

     return paymentService.paymentRequest(httpServletRequest);
  }

  @PostMapping(path = "/callback")
  public ResponseEntity<ReqRes<Object>> checkThePaymentCallback(@RequestParam Map<String, String> values,
      HttpServletRequest request) throws Exception {
    return paymentCallbackProcessor.paymentCallbackProcessor(values, request);
  }

  @PostMapping("/failed")
  public void saveFailedOrders(@RequestBody Map<String, String> values) {
    JSONObject options = new JSONObject();
    long user = 1;
    // System.out.println(values.get("razorpay_order_id"));

    options.put("razorpay_order_id", values.get("razorpay_order_id"));
    options.put("user", user);
    options.put("paymentType", PaymentType.NOTPAID);
    RazorpayTransectionIdsDto orderDetails = new RazorpayTransectionIdsDto(options);
    // TODO : i have to handle all the failed orders
    // kafkaService.sendMessage("order_topic", orderDetails);
    return;
  }

  @GetMapping("/orders")
  public ResponseEntity<ReqRes<List<OrderDetailsSendDTO>>> getAllOrderedProducts(HttpServletRequest httpServletRequest,
      @RequestParam("page") Integer page) throws Exception {
    return orderService.getAlltheOrders(httpServletRequest);
  }

}
