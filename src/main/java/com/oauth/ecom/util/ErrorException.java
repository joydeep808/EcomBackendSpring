package com.oauth.ecom.util;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ErrorException extends Exception{
  private int statusCode = 500;
  public ErrorException(String message , int statusCode){
    super(message);
    this.statusCode = statusCode;
  }
}
