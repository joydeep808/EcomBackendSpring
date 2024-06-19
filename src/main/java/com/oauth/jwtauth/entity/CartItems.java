package com.oauth.jwtauth.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
  @ManyToOne(cascade = CascadeType.ALL , fetch = FetchType.EAGER)
  private Products product;
  private int quantity;
}
