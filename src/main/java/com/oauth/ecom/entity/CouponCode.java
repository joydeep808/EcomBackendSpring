package com.oauth.ecom.entity;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.oauth.ecom.entity.enumentity.DiscountType;
import com.oauth.ecom.util.LocalDateTimeDeserializer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "Couponcode")
@Data
@Builder
@AllArgsConstructor
public class CouponCode {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true )
  @NotNull(message = "Coupon code required" )
  private String couponCode;
  private long minValue;
  @NotNull(message = "Start Date is required")
  @JsonFormat(pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime startDate;
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(pattern = "yyyy-MM-dd")
  @NotNull(message = "End Date is required")
  private LocalDateTime endDate;
  @NotNull(message = "Stock should not be null")
  private Long stock;
  private Boolean isPaused;
  private Boolean categoryApplyed;
  private DiscountType discountType;
  @NotNull(message = "discount upto required")
  private float discountUpto;
  @NotNull(message = "discount percenage required")
  private float discountPercentage;
  private String discription;
  @OneToOne(cascade = CascadeType.ALL)
  @JsonBackReference
  @JsonIgnore
  private Category category;
  public CouponCode(){
    this.isPaused = false;
    this.categoryApplyed = false;
    this.discountType = DiscountType.PERCENTAGE;
  }
}
