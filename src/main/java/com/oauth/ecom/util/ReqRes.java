package com.oauth.ecom.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.oauth.ecom.entity.Products;
import com.oauth.ecom.entity.UserEntity;

import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Component
public class ReqRes {
  private int statusCode;
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
  public void sendSuccessResponse(int statusCode , String message , Object data){
    this.statusCode  = statusCode;
    this.message = message;
    this.data = data;
    this.isSuccess = true;
  }
  public void sendSuccessResponse(int statusCode , String message ){
    this.statusCode  = statusCode;
    this.message = message;
    this.isSuccess = true;
    
  }
  public void  sendErrorMessage (int statusCode , String message , String error){
    this.statusCode =statusCode;
    this.message =message;
    this.error =error;
    this.isSuccess = false;
  }
  public void  sendErrorMessage (int statusCode , String message ){
    this.statusCode =statusCode;
    this.message =message;
    this.isSuccess = false;
  }
  
}
