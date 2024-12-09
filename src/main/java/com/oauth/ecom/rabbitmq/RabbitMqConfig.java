package com.oauth.ecom.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableRabbit
public class RabbitMqConfig {
  
  public static final String ONBOARDING_EMAIL_QUEUE = "onboarding_email_queue";
  public static final String COUPON_CODE_QUEUE = "coupon_code_queue";
  public static final String ORDER_TOPIC = "order_topic";
  public static final String UPDATE_PRODUCT_QUEUE = "update_product_queue";
  public static final String ORDER_CONFIRMATION_QUEUE = "order_confirmation_queue";
  public static final String CART_QUEUE = "cart_queue";


  @Bean
  public Queue onBoardingEmailQueue(){
    return new Queue(ONBOARDING_EMAIL_QUEUE);
  }

  @Bean
  public Queue updateProductQueue(){
    return new Queue(UPDATE_PRODUCT_QUEUE);
  }
  @Bean
  public  Queue orderTopic(){
    return new Queue(ORDER_TOPIC);
  }
  @Bean
  public Queue orderConfirmationQueue(){
    return new Queue(ORDER_CONFIRMATION_QUEUE);
  }
  @Bean
  public Queue cartQueue(){
    return new Queue(CART_QUEUE);
  }
  @Bean
  public ObjectMapper objectMapper(){
    return new ObjectMapper();
  }
  @Bean
  public Queue couponCodeQueue(){
    return new Queue(COUPON_CODE_QUEUE);
  }
}
