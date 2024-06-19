package com.oauth.jwtauth.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.oauth.jwtauth.repository.CouponCodeRepo;
import com.oauth.jwtauth.services.kafka.KafkaService;
import com.oauth.jwtauth.services.redis.RedisService;

@Component
public class ScheduleTask {
  @Autowired RedisService redisService;
  @Autowired CouponCodeRepo couponCodeRepo;
  @Autowired KafkaService kafkaService;
  @Scheduled( cron = "0 */2 * ? * *")
  public void setCouponCodes(){ /// every 3 minutes the it will check the codes and set them 
    kafkaService.sendMessage("coupon_Code_Topic", "");
    return;
  }
}
