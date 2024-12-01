package com.oauth.ecom.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.oauth.ecom.rabbitmq.MessageSender;
import com.oauth.ecom.rabbitmq.RabbitMqConfig;
import com.oauth.ecom.repository.CouponCodeRepo;
import com.oauth.ecom.repository.OrderRepo;
// import com.oauth.ecom.services.kafka.KafkaService;
import com.oauth.ecom.services.order.OrderService;
import com.oauth.ecom.services.payment.PaymentService;
import com.oauth.ecom.services.redis.RedisService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ScheduleTask {
 private final RedisService redisService;
 private final CouponCodeRepo couponCodeRepo;
 private final PaymentService paymentService;
 private final OrderRepo orderRepo;
 private final OrderService orderService;
  private final MessageSender messageSender;
  @Scheduled(cron = "0 */2 * ? * *")
  public void setCouponCodes(){ 
    messageSender.sendMessageToQueue(RabbitMqConfig.COUPON_CODE_QUEUE , "");
    return;
  }
  

  @Scheduled(cron ="* * 1 * * *" )
  public void successFailedOrders(){
    orderService.successTheFailedOrdersInScheduler();
  }

  @Scheduled(cron = "* * 2 * * *")
  public void deleteThePendingOrders(){
    
  }
  
}
