// package com.oauth.ecom.kafka.kafkaconsumers;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.stereotype.Service;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.oauth.ecom.dto.order.RazorpayTransectionIdsDto;
// import com.oauth.ecom.entity.enumentity.PaymentType;
// import com.oauth.ecom.services.order.OrderService;

// @Service
// public class OrderConsumer {
//   private final ObjectMapper objectMapper = new ObjectMapper();
//   @Autowired OrderService orderService;
//   @KafkaListener(topics = "order_topic")
//   public void consumeTheOrder(String message) throws Exception{
//     RazorpayTransectionIdsDto details =  objectMapper.readValue(message, RazorpayTransectionIdsDto.class);
//   orderService.purchaseAnOrder(
//     details.getUser(), 
//     details.getRazorpay_order_id(),
//     details.getPaymentType().equals(PaymentType.PAID) ? "PAID" : "NOTPAID"
//   );
//   }
// }
