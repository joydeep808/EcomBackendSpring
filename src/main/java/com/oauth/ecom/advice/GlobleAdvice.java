package com.oauth.ecom.advice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.oauth.ecom.entity.exceptions.MainException;
import com.oauth.ecom.util.ErrorException;
import com.oauth.ecom.util.ReqRes;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobleAdvice {
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Object> handleValidationExceptions(
    MethodArgumentNotValidException ex) {
Map<String, Object> errors = new HashMap<>();
errors.put("status", HttpStatus.BAD_REQUEST.value());
errors.put("message", "Validation error");
errors.put("errors", ex
.getBindingResult()
.getFieldErrors()
.stream()
.map(error -> error.getDefaultMessage()).collect(Collectors.toList()));
return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
}

@ExceptionHandler(ConstraintViolationException.class)

public ResponseEntity<Object> handleConstraintViolationException(
    ConstraintViolationException ex) {
Map<String, Object> errors = new HashMap<>();
errors.put("status", HttpStatus.BAD_REQUEST.value());
errors.put("message", "Validation error");
errors.put("isSuccess", false);
errors.put("errors", ex.getConstraintViolations().stream()
        .map(ConstraintViolation::getMessage).collect(Collectors.toList()));
return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
}
@ExceptionHandler(Exception.class)
public ResponseEntity<ReqRes<Object>> handleException(Exception ex){
  return new ReqRes<Object>().sendErrorMessage(500,ex.getLocalizedMessage() ).sendResponseEntity();
}
@ExceptionHandler(ErrorException.class)
public ResponseEntity<ReqRes<Object>> handleErrorException(ErrorException ex){
  return new ReqRes<Object>().sendErrorMessage(ex.getStatusCode(),ex.getMessage() ).sendResponseEntity();
}
@ExceptionHandler(MainException.class)
public ResponseEntity<ReqRes<Object>> handleMainException(MainException e){
  return new ReqRes<>().sendErrorMessage(e.getStatusCode(), e.getMessage()).sendResponseEntity();

}

}
