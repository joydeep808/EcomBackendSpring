package com.oauth.ecom.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="CartItems")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItems {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE )
  private Long id;
  @Column(name = "cart_id")
  private Long cart;
  private Long productId;
  private int quantity;
}
