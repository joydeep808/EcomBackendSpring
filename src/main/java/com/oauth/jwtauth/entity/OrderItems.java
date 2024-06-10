package com.oauth.jwtauth.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "OrderItems" , indexes  = {@Index(name="idx_order",columnList = "orderId")} )
@Data
public class OrderItems {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(cascade = CascadeType.ALL )
  @JoinColumn(name = "orderId")
  private Order order ;
  private int quantity;
  private double Totalprice;
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "Product")
  @JsonBackReference
  private Products products;
  private String color;
  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;
  public OrderItems(){
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }
  
}
