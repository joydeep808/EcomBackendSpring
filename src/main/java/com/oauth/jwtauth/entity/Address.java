package com.oauth.jwtauth.entity;



import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name="Address" )
@Data
public class Address {
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(cascade = CascadeType.ALL)
  @NotNull(message = "User is requird")
  @JsonBackReference
  private UserEntity user;
  @NotNull(message="Full address is required")
  private String fullAddress;
  
  @NotNull(message = "State is required")
  private String state;
  @NotNull(message = "Phone is required")

  private Long phone;
  @NotNull(message = "Pincode is required")
  private int pincode;
  private boolean isPrimery;
  public Address(){
    this.isPrimery = false;
  }
}
