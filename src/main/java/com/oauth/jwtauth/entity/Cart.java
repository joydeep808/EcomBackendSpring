package com.oauth.jwtauth.entity;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "Cart")
@Data
public class Cart {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @OneToOne(cascade = CascadeType.ALL , orphanRemoval=true)
  @JsonIgnore
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private UserEntity user;
  @OneToMany(cascade = CascadeType.ALL ,mappedBy="cart" ,orphanRemoval = true , fetch = FetchType.EAGER)
  @JsonManagedReference
  List<CartItems> cartItems;
  private float cartTotal;
  @ManyToOne(cascade = CascadeType.ALL)
  
  private CouponCode couponCode;
  private float discountCartTotal;
}