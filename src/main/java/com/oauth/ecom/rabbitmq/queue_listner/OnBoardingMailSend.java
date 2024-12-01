package com.oauth.ecom.rabbitmq.queue_listner;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauth.ecom.dto.userdto.UserEmailSendDto;
import com.oauth.ecom.rabbitmq.RabbitMqConfig;
import com.oauth.ecom.services.email.emailtemplate.EmailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OnBoardingMailSend {
  private final ObjectMapper objectMapper;
  private final EmailService emailService;

  @RabbitListener(queues = RabbitMqConfig.ONBOARDING_EMAIL_QUEUE)
  public void SendEmail(String message) throws JsonMappingException, JsonProcessingException{
    UserEmailSendDto mailRequest = objectMapper.readValue(message, UserEmailSendDto.class);
    emailService.welcomEmailSend(mailRequest.getEmail() ,mailRequest.getName());
  }

}
