package com.oauth.ecom.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.oauth.ecom.dto.order.RazorpayTransectionIdsDto;
import com.oauth.ecom.entity.enumentity.PaymentType;
import com.oauth.ecom.services.kafka.KafkaService;
import com.oauth.ecom.services.order.OrderService;
import com.oauth.ecom.services.payment.PaymentService;
import com.oauth.ecom.util.JwtInterceptor;
import com.oauth.ecom.util.ReqRes;
import com.razorpay.Utils;

import jakarta.servlet.http.HttpServletRequest;
@RestController
@RequestMapping("/api/v1/order")
@CrossOrigin(origins = "*" , maxAge = 3600)
public class OrderController {

  @Value("${spring.razorpay.secret}")
  private String razorpaySecret;
  @Autowired PaymentService paymentService;
  @Autowired JwtInterceptor jwtInterceptor;
  @Autowired OrderService orderService;
  @Autowired KafkaService kafkaService;
  @GetMapping(path = "/request" ,produces=MediaType.APPLICATION_JSON_VALUE )
  public ResponseEntity<ReqRes> paymentRequest (HttpServletRequest httpServletRequest) throws Exception{
    
  ReqRes paymentRequestDto = paymentService.paymentRequest(httpServletRequest);
   return ResponseEntity.ok(paymentRequestDto);
  }
  @PostMapping(path = "/callback" , consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ResponseEntity<ReqRes> checkThePaymentCallback(@RequestParam Map<String , String> values, HttpServletRequest request ) throws Exception{
    ReqRes response  = new ReqRes();

    // long user = jwtInterceptor.getIdFromJwt(request);


    // return ResponseEntity.ok(values);
    long user = 1;
    JSONObject options = new JSONObject();
    options.put("razorpay_order_id", values.get("razorpay_order_id"));
    options.put("razorpay_signature", values.get("razorpay_signature"));
    options.put("razorpay_payment_id", values.get("razorpay_payment_id"));
    boolean status = Utils.verifyPaymentSignature(options ,razorpaySecret );
    if (status) {
    boolean isThere = orderService.isOrderAlreadyThere(values.get("razorpay_order_id"));
    if (isThere) {
      response.sendSuccessResponse(200, "Order successfully placed");
      return ResponseEntity.status(200).body(response);
    }
      options.put("user", user);
      options.put("paymentType", PaymentType.PAID);
      RazorpayTransectionIdsDto oDetails = new RazorpayTransectionIdsDto(options);
     ReqRes serviceResponse =  paymentService.processTheOrder(oDetails);
      return ResponseEntity.status(serviceResponse.getStatusCode()).body(serviceResponse);
    }
    response.sendErrorMessage(401, "Unauthorized transection" );
    return ResponseEntity.status(401).body(response);
    }
    @PostMapping("/failed")
    public void saveFailedOrders(@RequestBody Map<String , String> values){
      JSONObject options = new JSONObject();
      long user = 1;
      System.out.println(values.get("razorpay_order_id"));
      
      options.put("razorpay_order_id", values.get("razorpay_order_id"));
      options.put("user", user);
      options.put("paymentType", PaymentType.NOTPAID);
      RazorpayTransectionIdsDto orderDetails = new RazorpayTransectionIdsDto(options);
      kafkaService.sendMessage("order_topic", orderDetails);
      return;
    }
  @GetMapping("/orders")
  public ResponseEntity<ReqRes> getAllOrderedProducts(HttpServletRequest httpServletRequest) throws Exception{
    ReqRes response = orderService.getAlltheOrders(httpServletRequest);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
  


}
