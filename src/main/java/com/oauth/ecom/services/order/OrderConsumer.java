package com.oauth.ecom.services.order;

import org.springframework.kafka.annotation.KafkaListener;

public class OrderConsumer {
// private final ObjectMapper objectMapper = new ObjectMapper();
// @Autowired UserRepo userRepo;
// @Autowired OrderRepo orderRepo;
// @Autowired CartRepo cartRepo;
  @KafkaListener(topics="order_topic" , groupId = "ecom_group")
  public void orderConsumer(String message){
    
  }
}
