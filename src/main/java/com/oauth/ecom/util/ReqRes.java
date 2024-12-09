package com.oauth.ecom.util;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Component
public class ReqRes<T> {
  private int statusCode;
  private String message;
  private Boolean isSuccess;
  private T data;
  public ReqRes(){
    this.isSuccess = false;
  }
  
  public ReqRes<T> sendSuccessResponse(int statusCode , String message ){
    this.statusCode  = statusCode;
    this.message = message;
    this.isSuccess = true;
    return this;
  }

  public ReqRes<T> sendSuccessResponse(int statusCode , String message , T data ){
    this.statusCode  = statusCode;
    this.message = message;
    this.isSuccess = true;
    this.data = data;
    return this;
  }


  public ReqRes<T>  sendErrorMessage (int statusCode , String message ){
    this.statusCode =statusCode;
    this.message =message;
    this.isSuccess = false;
    return this;
  }

  public ReqRes<T>  sendErrorMessage (int statusCode , String message ,  T data ){
    this.statusCode =statusCode;
    this.message =message;
    this.data = data;
    this.isSuccess = false;
    return this;
  }
  public ResponseEntity<ReqRes<T>> sendResponseEntity(){
    return ResponseEntity.status(statusCode).body(this);
  }

  
}
