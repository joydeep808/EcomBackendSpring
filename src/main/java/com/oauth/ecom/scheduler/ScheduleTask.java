package com.oauth.ecom.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.oauth.ecom.repository.CouponCodeRepo;
import com.oauth.ecom.repository.OrderRepo;
import com.oauth.ecom.services.kafka.KafkaService;
import com.oauth.ecom.services.order.OrderService;
import com.oauth.ecom.services.payment.PaymentService;
import com.oauth.ecom.services.redis.RedisService;

@Component
public class ScheduleTask {
  @Autowired RedisService redisService;
  @Autowired CouponCodeRepo couponCodeRepo;
  @Autowired KafkaService kafkaService;
  @Autowired PaymentService paymentService;
  @Autowired OrderRepo orderRepo;
  @Autowired OrderService orderService;
  @Scheduled( cron = "0 */2 * ? * *")
  public void setCouponCodes(){ 
    kafkaService.sendMessage("coupon_Code_Topic", "");
    return;
  }
  

  @Scheduled(cron ="* * 1 * * *" )
  public void successFailedOrders(){
    orderService.successTheFailedOrders();
  }

  @Scheduled(cron = "* * 2 * * *")
  public void deleteThePendingOrders(){
    
  }
  
}
