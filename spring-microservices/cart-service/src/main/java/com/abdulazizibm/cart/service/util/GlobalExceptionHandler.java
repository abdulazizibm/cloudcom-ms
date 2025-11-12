package com.abdulazizibm.cart.service.util;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleException(Exception ex) {
    Map<String, String> error = new HashMap<>();

    error.put("error", ex.getClass().getSimpleName());
    error.put("message", ex.getMessage() != null ? ex.getMessage() : "No message available");

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(error);

  }
}
