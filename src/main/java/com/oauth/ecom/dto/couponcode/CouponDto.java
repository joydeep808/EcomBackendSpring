package com.oauth.ecom.dto.couponcode;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.oauth.ecom.entity.enumentity.DiscountType;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class CouponDto  {
  @NotNull(message = "Coupon code")
  private String couponCode;
  @NotNull(message = "Start Date is required")

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
