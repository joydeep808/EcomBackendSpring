package com.oauth.ecom.dto.order;

import com.oauth.ecom.entity.CouponCode;

import lombok.Data;

@Data
public class CouponCodeValidationForOrder {
  
  private CouponCode couponCode;
  private float discountedAmount;
}
