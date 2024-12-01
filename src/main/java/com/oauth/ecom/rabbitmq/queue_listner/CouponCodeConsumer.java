package com.oauth.ecom.rabbitmq.queue_listner;
import java.util.List;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.oauth.ecom.entity.CouponCode;
import com.oauth.ecom.rabbitmq.RabbitMqConfig;
import com.oauth.ecom.repository.CouponCodeRepo;
import com.oauth.ecom.services.redis.RedisService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponCodeConsumer {
  private final RedisService redisService;
  private final CouponCodeRepo couponCodeRepo;

  @RabbitListener(queues = RabbitMqConfig.COUPON_CODE_QUEUE)
  public void CouponCodeListner() {
    List<CouponCode> couponCodes = couponCodeRepo.findAll();
    if (couponCodes == null) {
      return;
    }
    couponCodes.stream().map((coupon) -> redisService.saveCouponCode(coupon.getCouponCode()));
  }

}
