package com.oauth.ecom.services.payment;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.oauth.ecom.dto.order.PaymentRequestDto;
import com.oauth.ecom.dto.order.RazorpayTransectionIdsDto;
import com.oauth.ecom.entity.Cart;
import com.oauth.ecom.repository.CartRepo;
import com.oauth.ecom.services.kafka.KafkaService;
import com.oauth.ecom.util.JwtInterceptor;
import com.oauth.ecom.util.ReqRes;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Refund;
import jakarta.servlet.http.HttpServletRequest;
@Service
public class PaymentService {
  @Autowired CartRepo cartRepo;
  @Autowired JwtInterceptor jwtInterceptor;
  @Autowired KafkaService kafkaService;
  private final RazorpayClient razorpayClient;
  @Autowired
  public PaymentService(RazorpayClient razorpayClient){
  this.razorpayClient = razorpayClient;
}



public ReqRes paymentRequest(HttpServletRequest httpServletRequest) throws Exception{
    
    ReqRes response = new ReqRes();
    //  long id =  jwtInterceptor.getIdFromJwt(httpServletRequest);
    long id = 1;
    Cart cart = cartRepo.findByUser(id);
    JSONObject orderRequest = new JSONObject();
    orderRequest.put("amount",cart.getDiscountCartTotal() * 100);
    orderRequest.put("currency", "INR");
    orderRequest.put("receipt", cart.getUser().getEmail());
    Order order = razorpayClient.orders.create(orderRequest);
   PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
   paymentRequestDto.setAmount(order.get("amount"));
   paymentRequestDto.setId(order.get("id"));
   paymentRequestDto.setCurrency(order.get("currency"));
    response.sendSuccessResponse(200, "Successfully done", paymentRequestDto);
    return response;
}

public ReqRes processTheOrder(RazorpayTransectionIdsDto details){
  ReqRes response = new ReqRes();
  try {
    kafkaService.sendMessage("order_topic",details );
    response.sendSuccessResponse(200, "Order placed successfully done");
    return response;
  } catch (Exception e) {
    response.sendErrorMessage(500,"We are facing some issue" , e.getMessage());
    return response;
  }
}
public ReqRes refundMoney(String id) throws Exception{
  ReqRes response =  new ReqRes();

 try {
   Refund  refund = razorpayClient.payments.refund(id);
   System.out.println(refund);
   response.sendSuccessResponse(200, "Payment was refunded");
   return response;
 } catch (Exception e) {
  response.sendErrorMessage(400, "unexpected " , String.valueOf(e));
  return response;
  
 }
 

}


public boolean fetchPaymentByOrderId(String id){
  try {
    razorpayClient.payments.fetch(id);
    return true;
  } catch (Exception e) {
    return false;
  }
}
public ReqRes checkRefundStatus(String id) {
  ReqRes response = new ReqRes();
 try {
   Refund refundStatus = razorpayClient.refunds.fetch(id);
   response.sendSuccessResponse(200, refundStatus.get("status"));
  //  {"speed_processed":"normal","amount":10000,"speed_requested":"optimum","notes":[],"batch_id":null,"created_at":1708957367,"acquirer_data":{"arn":"10000000000000"},"payment_id":"pay_NcGvUU1sIXT1lf","currency":"INR","receipt":null,"id":"rfnd_NfXW1oidnhLNH9","entity":"refund","status":"processed"}
   return response;
 } catch (Exception e) {
  response.sendErrorMessage(500, e.getMessage() );
  return response;
 }
}
}

