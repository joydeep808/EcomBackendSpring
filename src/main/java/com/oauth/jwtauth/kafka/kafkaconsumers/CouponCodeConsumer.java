package com.oauth.jwtauth.kafka.kafkaconsumers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.oauth.jwtauth.entity.CouponCode;
import com.oauth.jwtauth.repository.CouponCodeRepo;
import com.oauth.jwtauth.services.redis.RedisService;

@Service
public class CouponCodeConsumer {
  @Autowired CouponCodeRepo couponCodeRepo;
  @Autowired RedisService redisService;
  @KafkaListener(topics="coupon_Code_Topic")
  public void CouponCodeListner(){
    List<CouponCode > couponCodes = couponCodeRepo.findAll();
    if (couponCodes == null) {
      return;
    }
    for (CouponCode couponCode : couponCodes) {
      redisService.saveCouponCode(couponCode.getCouponCode());
    }
    return;
  }
}
