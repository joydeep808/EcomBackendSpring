package com.oauth.ecom.entity.exceptions.thrown_exception;

import com.oauth.ecom.entity.exceptions.MainException;

public class ProductNotFound extends MainException{
  public ProductNotFound(String message , Integer statusCode){
    super(message, statusCode);
  }
}
