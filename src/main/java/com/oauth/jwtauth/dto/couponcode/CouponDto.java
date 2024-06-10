package com.oauth.jwtauth.dto.couponcode;

import java.time.LocalDateTime;

import com.oauth.jwtauth.entity.enumentity.DiscountType;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class CouponDto  {
  @NotNull(message = "Coupon code")
  private String couponCode;
  @NotNull(message = "Start Date is required")
  private LocalDateTime startDate;
  @NotNull(message = "End Date is required")
  private LocalDateTime endDate;
  @NotNull(message = "Stock should not be null")
  private Long stock;
  private Boolean isPaused;
  private Boolean categoryApplyed;
  private DiscountType discountType;
  private float discountUpto;
  private String discription;
 
}
