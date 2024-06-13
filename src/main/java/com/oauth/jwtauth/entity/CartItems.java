package com.oauth.jwtauth.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="CartItems")
@Data
public class CartItems {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE )
  private Long id;
  @ManyToOne
  @JsonBackReference
  private Cart cart;
  @ManyToOne(cascade = CascadeType.ALL)
  private Products product;
  private int quantity;
  @JsonFormat(pattern = "yyyy-MM-dd")

  @CreatedDate
  private LocalDateTime createdAt;
  @LastModifiedDate
@JsonFormat(pattern = "yyyy-MM-dd")

  private LocalDateTime updatedAt;
  public CartItems(){
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }
}
