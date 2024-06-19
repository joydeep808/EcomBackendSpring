package com.oauth.jwtauth.services.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {
  @Autowired(required = true)
  private KafkaTemplate<String , Object> kafkaTemplate;
  public void sendMessage(String topic , Object message ){
    try {
      Message<Object> message2  = MessageBuilder.withPayload(message).setHeader(KafkaHeaders.TOPIC, topic).build();
      // objectMapper.registerModule(new JavaTimeModule());
      // // objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
      // byte[] valueBytes = objectMapper.writeValueAsBytes(message);
      kafkaTemplate.send(message2);
    } catch (Exception e) {
     System.out.println(e.getLocalizedMessage());
    }
  }

}
