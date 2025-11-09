package com.abdulazizibm.user.communication.service.exception;

import static java.text.MessageFormat.format;

public class UserAlreadyExistsException extends RuntimeException{

  public UserAlreadyExistsException(String userEmail) {
    super(format("User with e-mail {0} is already registered", userEmail));
  }
}
