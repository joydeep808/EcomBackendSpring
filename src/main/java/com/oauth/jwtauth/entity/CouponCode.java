package com.oauth.jwtauth.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oauth.jwtauth.entity.enumentity.DiscountType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "Couponcode")
@Data
public class CouponCode {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true )
  @NotNull(message = "Coupon code")
  @Size(min = 6 , max =  20 , message = "couponCode should be within 6-20 digits")
  private String couponCode;
  @NotNull(message = "Start Date is required")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

  private LocalDateTime startDate;
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

  @NotNull(message = "End Date is required")
  private LocalDateTime endDate;
  @NotNull(message = "Stock should not be null")
  private Long stock;
  private Boolean isPaused;
  private Boolean categoryApplyed;
  private DiscountType discountType;
  private float discountUpto;
  private String discription;
  @OneToOne(cascade = CascadeType.ALL)
  @JsonBackReference
  @JsonIgnore
  private Category category;
  @CreatedDate
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

  private LocalDateTime createdAt;
  @LastModifiedDate
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

  private LocalDateTime updatedAt;
  public CouponCode(){
    this.isPaused = false;
    this.categoryApplyed = false;
    this.discountType = DiscountType.PERCENTAGE;
  }
}
