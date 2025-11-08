package com.abdulazizibm.order.service.exception;

import static java.text.MessageFormat.format;

public class OrderNotFoundException extends RuntimeException{

  public OrderNotFoundException(String userEmail) {
    super(format("No order found for user {0}", userEmail));
  }
}
