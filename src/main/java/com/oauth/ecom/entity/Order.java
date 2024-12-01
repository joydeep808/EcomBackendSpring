package com.oauth.ecom.entity;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.oauth.ecom.entity.enumentity.OrderStatus;
import com.oauth.ecom.entity.enumentity.PaymentType;
import com.oauth.ecom.util.LocalDateTimeDeserializer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
@Entity
@Table(name = "Orders")
@Data
@AllArgsConstructor
@Builder
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Enumerated(EnumType.STRING)
  private OrderStatus status;
  @ManyToOne(cascade = CascadeType.ALL)
  @JsonBackReference
  private UserEntity user;
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "addressId")
  @JsonBackReference
  private Address addressId;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDateTime expectedDeleveryDate;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDateTime deleveryDate;
  
  // Transection
  private String transectionId;
  private PaymentType paymentType;
  private float totalAmount;
  private float discountAmount;
  private float netAmount;
  /// Delevery 
  private float shipingAmount;
  private String trackingId;
  @ManyToOne(cascade = CascadeType.ALL)
  @JsonIgnore
  private CouponCode couponCode;


  // date
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)

  @CreatedDate
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

  private LocalDateTime createdAt;
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
@JsonDeserialize(using = LocalDateTimeDeserializer.class)

  @LastModifiedDate
  private LocalDateTime updatedAt;
  public Order(){
    this.status = OrderStatus.PLACED;
    this.createdAt = LocalDateTime.now();
    this.shipingAmount = 50;
    this.discountAmount = 0;
    this.paymentType = PaymentType.NOTPAID;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }
}
