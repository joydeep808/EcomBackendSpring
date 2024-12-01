package com.oauth.ecom.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.oauth.ecom.dto.order.RazorpayTransectionIdsDto;
import com.oauth.ecom.entity.enumentity.PaymentType;
import com.oauth.ecom.services.order.OrderService;
import com.oauth.ecom.services.order.PaymentCallbackProcessor;
import com.oauth.ecom.services.payment.PaymentService;
import com.oauth.ecom.util.JwtInterceptor;
import com.oauth.ecom.util.ReqRes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/order")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class OrderController {
  private final PaymentService paymentService;
  private final OrderService orderService;
  private final PaymentCallbackProcessor paymentCallbackProcessor;
  // @Autowired KafkaService kafkaService;
  @GetMapping(path = "/request", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReqRes> paymentRequest(HttpServletRequest httpServletRequest) throws Exception {

    ReqRes paymentRequestDto = paymentService.paymentRequest(httpServletRequest);
    return ResponseEntity.ok(paymentRequestDto);
  }

  @PostMapping(path = "/callback", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ResponseEntity<ReqRes> checkThePaymentCallback(@RequestParam Map<String, String> values,
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
  public ResponseEntity<ReqRes> getAllOrderedProducts(HttpServletRequest httpServletRequest,
      @RequestParam("page") Integer page) throws Exception {
    ReqRes response = orderService.getAlltheOrders(httpServletRequest);
    return response.getIsSuccess() ? ResponseEntity.status(response.getStatusCode()).body(response)
        : ResponseEntity.status(response.getStatusCode()).body(response);
  }

}
