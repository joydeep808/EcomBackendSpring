package com.oauth.ecom.services.order;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oauth.ecom.dto.order.RazorpayTransectionIdsDto;
import com.oauth.ecom.entity.enumentity.PaymentType;
import com.oauth.ecom.services.payment.PaymentService;
import com.oauth.ecom.util.JwtInterceptor;
import com.oauth.ecom.util.ReqRes;
import com.razorpay.Utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentCallbackProcessor {

  private final OrderService orderService;
  private final PaymentService paymentService;
  private final JwtInterceptor jwtInterceptor;
  @Value("${spring.razorpay.secret}")
  private String razorpaySecret;

  public ResponseEntity<ReqRes<Object>> paymentCallbackProcessor(Map<String, String> values, HttpServletRequest request) throws Exception {
    ReqRes<Object> response = new ReqRes<>();

    // The user ID is obtained from the JWT token
    Integer user = jwtInterceptor.getIdFromJwt(request);

    // The values map contains the following keys:
    // - razorpay_order_id
    // - razorpay_signature
    // - razorpay_payment_id
    // return ResponseEntity.ok(values);

    // For testing purposes, the user ID is hardcoded to 1

    System.err.println(values.get("razorpay_order_id"));

    // Create a JSONObject from the request parameters
    JSONObject options = new JSONObject();
    options.put("razorpay_order_id", values.get("razorpay_order_id"));
    options.put("razorpay_signature", values.get("razorpay_signature"));
    options.put("razorpay_payment_id", values.get("razorpay_payment_id"));
    System.err.println(options.get("razorpay_order_id"));
    // Verify the payment signature using the Razorpay Utils class
    boolean status = Utils.verifyPaymentSignature(options, razorpaySecret);

    // If the signature is valid, check if the order already exists in the
    // database
    if (status) {
      boolean isThere = orderService.isOrderAlreadyThere(values.get("razorpay_order_id"));
      if (isThere) {
        // If the order already exists, return a success response
        return response.sendSuccessResponse(200, "Order successfully placed").sendResponseEntity();
      }

      // If the order does not exist, create a new RazorpayTransectionIdsDto object
      // and set the user ID and payment type
      options.put("user",user.toString());
      options.put("paymentType", PaymentType.PAID);
      RazorpayTransectionIdsDto oDetails = new RazorpayTransectionIdsDto(options);

      // Call the processTheOrder method of the PaymentService to process the
      // order
     return paymentService.processTheOrder(oDetails);
      // Return the result of the order processing
    }

    // If the signature is invalid, return an unauthorized response
    response.sendErrorMessage(401, "Unauthorized Transection");
    return ResponseEntity.status(401).body(response);
  }

}
