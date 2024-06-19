package com.oauth.jwtauth.dto.order;

import com.oauth.jwtauth.entity.CouponCode;

import lombok.Data;

@Data
public class CouponCodeValidationForOrder {
  
  private CouponCode couponCode;
  private float discountedAmount;
}
