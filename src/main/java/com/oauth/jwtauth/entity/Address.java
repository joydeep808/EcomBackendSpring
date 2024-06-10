package com.oauth.jwtauth.entity;



import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
}
