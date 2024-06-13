package com.oauth.jwtauth.services.cart;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CartKafkaConsumer {
  private final ObjectMapper objectMapper = new ObjectMapper();
  @KafkaListener(topics = "cart_topic" ,groupId = "ecom_group")
  public void kafkaListner(String message) {
    try {
      Object valueObject =  objectMapper.readValue(message, Object.class);
      System.out.println("Message" + valueObject);
  
    } catch (Exception e) {
      System.out.println(e.getLocalizedMessage());
    }
  }
}
