package com.oauth.ecom.services.payment;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oauth.ecom.dto.order.*;
import com.oauth.ecom.entity.Cart;
import com.oauth.ecom.entity.enumentity.PaymentType;
import com.oauth.ecom.repository.CartRepo;
import com.oauth.ecom.services.order.OrderService;
// import com.oauth.ecom.services.kafka.KafkaService;
import com.oauth.ecom.util.*;
import com.razorpay.*;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PaymentService {

  @Autowired
  CartRepo cartRepo;
  @Autowired
  JwtInterceptor jwtInterceptor;
  // @Autowired KafkaService kafkaService;
  private final RazorpayClient razorpayClient;
  private final OrderService orderService;


  public ResponseEntity<ReqRes<PaymentRequestDto>> paymentRequest(HttpServletRequest httpServletRequest) throws Exception {

    ReqRes<PaymentRequestDto> response = new ReqRes<PaymentRequestDto>();

    // Get the user id from the JWT token
    long id = jwtInterceptor.getIdFromJwt(httpServletRequest);
    String email  = jwtInterceptor.getEmailFromJwt(httpServletRequest);
    // Get the cart object for the given user id
    Cart cart = cartRepo.findByUser(id);

    // Create a new JSONObject which will be used to create a new order in Razorpay
    JSONObject orderRequest = new JSONObject();

    // Set the amount of the order in paise. We are multiplying the amount by 100
    // because Razorpay expects the amount in paise.
    orderRequest.put("amount", cart.getDiscountCartTotal() * 100);

    // Set the currency of the order. We are using INR as the currency.
    orderRequest.put("currency", "INR");

    // Set the receipt of the order. We are using the user's email address as the
    // receipt.
    orderRequest.put("receipt", cart.getUser().getId().toString()+"USER");

    // Create a new order in Razorpay using the orderRequest JSONObject.
    Order order = razorpayClient.orders.create(orderRequest);

    // Create a new PaymentRequestDto object which will be used to send the order
    // details to the client.
    PaymentRequestDto paymentRequestDto = new PaymentRequestDto();

    // Set the amount of the order in the PaymentRequestDto object.
    paymentRequestDto.setAmount(order.get("amount"));

    // Set the id of the order in the PaymentRequestDto object.
    paymentRequestDto.setId(order.get("id"));
    paymentRequestDto.setEmail(email);
    // Set the currency of the order in the PaymentRequestDto object.
    paymentRequestDto.setCurrency(order.get("currency"));

    // Send the PaymentRequestDto object as the response to the client.
    return response.sendSuccessResponse(200, "Successfully done", paymentRequestDto).sendResponseEntity();
  }

  public ResponseEntity<ReqRes<Object>> processTheOrder(RazorpayTransectionIdsDto details) throws Exception {
    // Initialize the response object that will be used to send the result of the operation
    ReqRes<Object> response = new ReqRes<Object>();
      // Process the order and save it to the database
      // The message to be sent to the queue has been commented out
      // messageSender.sendMessageToQueue(RabbitMqConfig.ORDER_TOPIC,
      // objectMapper.writeValueAsString(details));

      // Call the purchaseAnOrder method from the orderService
      // Pass the user ID, Razorpay order ID, and payment type as parameters
      // Convert the payment type to "PAID" or "NOTPAID" based on the enum value
      orderService.purchaseAnOrder(
        details.getUser(), 
        details.getRazorpay_order_id(),
        details.getPaymentType().equals(PaymentType.PAID) ? "PAID" : "NOTPAID"
      );

      // If the order is processed successfully, send a success response
     return response.sendSuccessResponse(200, "Order placed successfully done").sendResponseEntity();
   
  }

  /**
   * Refund money for a given payment ID
   * 
   * @param id The payment ID to refund
   * @return A response object containing the result of the refund operation
   * @throws Exception If there is an error during the refund operation
   */
  public ResponseEntity<ReqRes<Object>> refundMoney(String id) throws Exception {
    // Create a new response object which will be used to send the result of the
    // refund operation
    ReqRes<Object> response = new ReqRes<>();

      // Call the refund method of the Razorpay client
      // Pass the payment ID as a parameter
      razorpayClient.payments.refund(id);
      // If the refund is successful, send a success response
      // Set the status code to 200, and the message to "Payment was refunded"
      return response.sendSuccessResponse(200, "Payment was refunded").sendResponseEntity();
  }

  public boolean fetchPaymentByOrderId(String id) {
    try {
      razorpayClient.payments.fetch(id);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public ResponseEntity<ReqRes<Object>> checkRefundStatus(String id) throws RazorpayException {
    // Create a response object to encapsulate the result of the refund status check
      ReqRes<Object> response = new ReqRes<>();
      // Fetch the refund status from the Razorpay client using the provided refund ID
      Refund refundStatus = razorpayClient.refunds.fetch(id);

      // Send a success response with the refund status
      // The status is retrieved from the refundStatus object
     return  response.sendSuccessResponse(200, refundStatus.get("status")).sendResponseEntity();

      // Example of a refund status JSON response from Razorpay
      // {"speed_processed":"normal","amount":10000,"speed_requested":"optimum","notes":[],
      // "batch_id":null,"created_at":1708957367,"acquirer_data":{"arn":"10000000000000"},
      // "payment_id":"pay_NcGvUU1sIXT1lf","currency":"INR","receipt":null,
      // "id":"rfnd_NfXW1oidnhLNH9","entity":"refund","status":"processed"}

      // Return the response object containing the success status and refund details
   
  }
}
