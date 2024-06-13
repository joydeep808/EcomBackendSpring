package com.oauth.jwtauth.entity;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.oauth.jwtauth.entity.enumentity.OrderStatus;
import com.oauth.jwtauth.entity.enumentity.PaymentType;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "Orders")
@Data
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
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

  private LocalDateTime expectedDeleveryDate;
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

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



  // date
  @CreatedDate
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

  private LocalDateTime createdAt;
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

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
