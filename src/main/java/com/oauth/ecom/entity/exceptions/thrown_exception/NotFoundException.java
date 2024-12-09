package com.oauth.ecom.entity.exceptions.thrown_exception;

import com.oauth.ecom.entity.exceptions.MainException;

public class NotFoundException extends MainException{
  public NotFoundException(String message , Integer statusCode){
    super(message, statusCode);
  }
}
