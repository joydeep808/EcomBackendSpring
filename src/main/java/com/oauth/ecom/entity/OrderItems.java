package com.oauth.ecom.entity;

import java.math.BigInteger;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.oauth.ecom.util.LocalDateTimeDeserializer;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "OrderItems")
@Data
public class OrderItems {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "order_id")
  private Long order;
  private int quantity;
  private double Totalprice;
  private Long products;
  private String color;
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @CreatedDate
  @JsonFormat(shape = Shape.STRING)
  private LocalDateTime createdAt;
  @JsonFormat(shape = Shape.STRING)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)

  @LastModifiedDate
  private LocalDateTime updatedAt;
  public OrderItems(){
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }
  
}
