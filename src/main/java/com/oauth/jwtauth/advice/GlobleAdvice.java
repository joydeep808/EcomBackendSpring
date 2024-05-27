package com.oauth.jwtauth.advice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
errors.put("errors", ex.getConstraintViolations().stream()
        .map(ConstraintViolation::getMessage).collect(Collectors.toList()));
return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
}
}
