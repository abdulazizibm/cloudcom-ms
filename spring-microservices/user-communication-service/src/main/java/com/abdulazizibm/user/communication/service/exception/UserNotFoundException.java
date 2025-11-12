package com.abdulazizibm.user.communication.service.exception;

import static java.text.MessageFormat.format;


public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String userEmail) {
    super(format("User {0} ist not registered", userEmail));
  }
}
