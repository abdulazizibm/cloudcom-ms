package com.abdulazizibm.user.communication.service;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class OrderConfirmationListener {
  @SqsListener("order-confirmation-queue")
  public void handleOrderConfirmation(Map<String, Object> message){
    log.info("Received a message from queue: {}", message);

  }

}
