package com.oauth.ecom.services.couponcode;

import org.springframework.stereotype.Component;

import com.oauth.ecom.entity.CouponCode;
import com.oauth.ecom.repository.CouponCodeRepo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponCodeUtilService {
  private final CouponCodeRepo couponCodeRepo;

  public void updateCouponCodeQuantity(String couponCode ){
  CouponCode foundCouponCode = couponCodeRepo.findByCouponCode(couponCode).orElse(null);
  foundCouponCode.setStock(foundCouponCode.getStock() - 1);
  couponCodeRepo.save(foundCouponCode);
  return;
  }
}
