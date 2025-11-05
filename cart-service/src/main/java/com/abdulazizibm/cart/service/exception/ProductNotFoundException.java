package com.abdulazizibm.cart.service.exception;

import static java.text.MessageFormat.format;

public class ProductNotFoundException extends RuntimeException{

  public ProductNotFoundException(String productName, String userEmail) {
    super(format("{0} not found in cart of user {1}", productName, userEmail));
  }
}
