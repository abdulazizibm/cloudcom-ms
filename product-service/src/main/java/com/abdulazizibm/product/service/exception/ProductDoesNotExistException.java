package com.abdulazizibm.product.service.exception;

import static java.text.MessageFormat.format;

public class ProductDoesNotExistException extends RuntimeException{

  public ProductDoesNotExistException(String productName) {
    super(format("Product {0} is not found in CloudCom shop", productName));
  }
}
