package com.oauth.ecom.rabbitmq.queue_listner;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauth.ecom.dto.product.ProductParseDto;
import com.oauth.ecom.rabbitmq.RabbitMqConfig;
import com.oauth.ecom.services.cart.CartService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartListner {
  private final CartService cartService;
  private final ObjectMapper objectMapper;
  @RabbitListener(queues = RabbitMqConfig.CART_QUEUE)
  public void cartListner(String message) throws JsonMappingException, JsonProcessingException{
      ProductParseDto productParseDto = objectMapper.readValue(message, ProductParseDto.class);
      cartService.updateCart(productParseDto);
  }
}
