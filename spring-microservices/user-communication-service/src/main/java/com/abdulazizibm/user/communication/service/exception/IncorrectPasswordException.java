package com.abdulazizibm.user.communication.service.exception;

import static java.text.MessageFormat.format;

public class IncorrectPasswordException extends RuntimeException{

  public IncorrectPasswordException(String userEmail) {
    super(format("Incorrect password for user {0}", userEmail));
  }
}
