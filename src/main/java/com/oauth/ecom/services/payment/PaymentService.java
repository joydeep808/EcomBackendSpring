package com.oauth.ecom.services.payment;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauth.ecom.dto.order.*;
import com.oauth.ecom.entity.Cart;
import com.oauth.ecom.entity.enumentity.PaymentType;
import com.oauth.ecom.rabbitmq.MessageSender;
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

  private final MessageSender messageSender;
  @Autowired
  CartRepo cartRepo;
  @Autowired
  JwtInterceptor jwtInterceptor;
  // @Autowired KafkaService kafkaService;
  private final RazorpayClient razorpayClient;
  private final ObjectMapper objectMapper;
  private final OrderService orderService;


  public ReqRes paymentRequest(HttpServletRequest httpServletRequest) throws Exception {

    ReqRes response = new ReqRes();

    // Get the user id from the JWT token
    long id = jwtInterceptor.getIdFromJwt(httpServletRequest);

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
    orderRequest.put("receipt", cart.getUser().getEmail());

    // Create a new order in Razorpay using the orderRequest JSONObject.
    Order order = razorpayClient.orders.create(orderRequest);

    // Create a new PaymentRequestDto object which will be used to send the order
    // details to the client.
    PaymentRequestDto paymentRequestDto = new PaymentRequestDto();

    // Set the amount of the order in the PaymentRequestDto object.
    paymentRequestDto.setAmount(order.get("amount"));

    // Set the id of the order in the PaymentRequestDto object.
    paymentRequestDto.setId(order.get("id"));

    // Set the currency of the order in the PaymentRequestDto object.
    paymentRequestDto.setCurrency(order.get("currency"));

    // Send the PaymentRequestDto object as the response to the client.
    response.sendSuccessResponse(200, "Successfully done", paymentRequestDto);

    return response;
  }

  public ReqRes processTheOrder(RazorpayTransectionIdsDto details) {
    // Initialize the response object that will be used to send the result of the operation
    ReqRes response = new ReqRes();
    try {
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
      response.sendSuccessResponse(200, "Order placed successfully done");
      return response;
    } catch (Exception e) {
      // If an exception occurs, send an error response with the exception message
      response.sendErrorMessage(500, "We are facing some issue", e.getMessage());
      return response;
    }
  }

  /**
   * Refund money for a given payment ID
   * 
   * @param id The payment ID to refund
   * @return A response object containing the result of the refund operation
   * @throws Exception If there is an error during the refund operation
   */
  public ReqRes refundMoney(String id) throws Exception {
    // Create a new response object which will be used to send the result of the
    // refund operation
    ReqRes response = new ReqRes();

    try {
      // Call the refund method of the Razorpay client
      // Pass the payment ID as a parameter
      razorpayClient.payments.refund(id);
      // If the refund is successful, send a success response
      // Set the status code to 200, and the message to "Payment was refunded"
      response.sendSuccessResponse(200, "Payment was refunded");

      // Return the response object
      return response;
    } catch (Exception e) {
      // If an exception occurs, send an error response
      // Set the status code to 400, and the message to the exception message
      response.sendErrorMessage(400, "unexpected ", String.valueOf(e));

      // Return the response object
      return response;
    }
  }

  public boolean fetchPaymentByOrderId(String id) {
    try {
      razorpayClient.payments.fetch(id);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public ReqRes checkRefundStatus(String id) {
    // Create a response object to encapsulate the result of the refund status check
    ReqRes response = new ReqRes();
    try {
      // Fetch the refund status from the Razorpay client using the provided refund ID
      Refund refundStatus = razorpayClient.refunds.fetch(id);

      // Send a success response with the refund status
      // The status is retrieved from the refundStatus object
      response.sendSuccessResponse(200, refundStatus.get("status"));

      // Example of a refund status JSON response from Razorpay
      // {"speed_processed":"normal","amount":10000,"speed_requested":"optimum","notes":[],
      // "batch_id":null,"created_at":1708957367,"acquirer_data":{"arn":"10000000000000"},
      // "payment_id":"pay_NcGvUU1sIXT1lf","currency":"INR","receipt":null,
      // "id":"rfnd_NfXW1oidnhLNH9","entity":"refund","status":"processed"}

      // Return the response object containing the success status and refund details
      return response;
    } catch (Exception e) {
      // In case of an exception, send an error response with the exception message
      // The error message provides details about the failure
      response.sendErrorMessage(500, e.getMessage());

      // Return the response object containing the error details
      return response;
    }
  }
}
