package com.oauth.ecom.rabbitmq;

import java.util.Queue;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageSender {
  private final RabbitTemplate rabbitTemplate;

  public Boolean sendMessageToQueue(String queueName, String message){
   try {
    rabbitTemplate.convertAndSend(queueName, message);
    return true;
   } catch (Exception e) {
    return false;
   }
  }
}
