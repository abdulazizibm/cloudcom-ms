package com.abdulazizibm.cart.service.exception;

import static java.text.MessageFormat.format;

public class CartNotFoundException extends RuntimeException{

  public CartNotFoundException(String userEmail) {
    super(format("No cart found for user {0}", userEmail));
  }
}
