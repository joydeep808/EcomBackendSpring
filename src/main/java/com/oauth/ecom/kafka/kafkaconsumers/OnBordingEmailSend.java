package com.oauth.ecom.kafka.kafkaconsumers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauth.ecom.dto.userdto.UserEmailSendDto;
import com.oauth.ecom.services.email.emailtemplate.EmailService;

@Service
public class OnBordingEmailSend {
  ObjectMapper objectMapper = new ObjectMapper();
  @Autowired EmailService emailService;
  @KafkaListener(topics="welcome_Email_Send")
  public void SendEmail(String message) throws JsonMappingException, JsonProcessingException{
   UserEmailSendDto user =  objectMapper.readValue(message, UserEmailSendDto.class);
    emailService.welcomEmailSend(user.getEmail() , user.getName());
  }
}
