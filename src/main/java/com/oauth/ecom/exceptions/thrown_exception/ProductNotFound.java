package com.oauth.ecom.exceptions.thrown_exception;

import com.oauth.ecom.exceptions.MainException;

public class ProductNotFound extends MainException{
  public ProductNotFound(String message , Integer statusCode){
    super(message, statusCode);
  }
}
