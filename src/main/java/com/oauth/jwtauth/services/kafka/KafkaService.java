package com.oauth.jwtauth.services.kafka;

import java.text.SimpleDateFormat;

import javax.naming.event.ObjectChangeListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class KafkaService {
  @Autowired(required = true)
  private KafkaTemplate<String , Object> kafkaTemplate;
  private final ObjectMapper objectMapper = new ObjectMapper();
  public void sendMessage(String topic , Object message ){
    try {
      objectMapper.registerModule(new JavaTimeModule());
      // objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
      byte[] valueBytes = objectMapper.writeValueAsBytes(message);
      kafkaTemplate.send(topic , valueBytes);
    } catch (Exception e) {
     System.out.println(e.getLocalizedMessage());
    }
  }

}
