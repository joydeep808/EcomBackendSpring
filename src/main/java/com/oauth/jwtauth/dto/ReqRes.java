package com.oauth.jwtauth.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.oauth.jwtauth.entity.Products;
import com.oauth.jwtauth.entity.UserEntity;
import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ReqRes {
  private Integer statusCode;
  private String message;
  private String error;
  private Boolean isSuccess;
  private String token;
  private String refreshToken;
  private String expirationDate;
  private String name;
  private String eamil;
  private String password;
  private String username;
  private UserEntity user;
  private Products products;
  private List<Products> allProducts;
  private Object data;
  public ReqRes(){
    this.isSuccess = false;
  }
  
}
