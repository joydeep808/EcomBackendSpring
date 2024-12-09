package com.oauth.ecom.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.oauth.ecom.rabbitmq.*;
import com.oauth.ecom.services.order.OrderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ScheduleTask {
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
