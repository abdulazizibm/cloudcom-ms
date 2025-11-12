package com.abdulazizibm.payment.service;

import static java.text.MessageFormat.format;

import com.abdulazizibm.common.message.OrderCreatedMessage;
import com.abdulazizibm.common.message.PaymentDoneMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.QueueDoesNotExistException;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@RequiredArgsConstructor
@Slf4j
@Service
public class PaymentService {

  private final SqsClient sqsClient;
  private final ObjectMapper objectMapper;
  private final OrderServiceClient orderServiceClient;

  @Value("${aws.sqs.order.queue.name}")
  private String orderQueue;
  private String orderQueueUrl;

  @Value("${aws.sqs.payment.queue.name}")
  private String paymentQueue;
  private String paymentQueueUrl;

  @PostConstruct
  void init() {
    this.paymentQueueUrl = getPaymentQueueUrl();
    this.orderQueueUrl = getOrderQueueUrl();
  }

  private String getPaymentQueueUrl() {
    try {
      return sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
              .queueName(paymentQueue)
              .build())
          .queueUrl();
    } catch (QueueDoesNotExistException e) {
      val queue = sqsClient.createQueue(CreateQueueRequest.builder()
          .queueName(paymentQueue)
          .build());
      return queue.queueUrl();
    }
  }

  private String getOrderQueueUrl() {
    try {
      return sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
              .queueName(orderQueue)
              .build())
          .queueUrl();
    } catch (QueueDoesNotExistException e) {
      val queue = sqsClient.createQueue(CreateQueueRequest.builder()
          .queueName(orderQueue)
          .build());
      return queue.queueUrl();
    }
  }

  private void publishPaymentMessage(PaymentDoneMessage message) {
    try {
      String msg = objectMapper.writeValueAsString(message);

      val msgRequest = SendMessageRequest.builder()
          .queueUrl(paymentQueueUrl)
          .messageBody(msg)
          .build();
      sqsClient.sendMessage(msgRequest);
      log.info(format("Successfully send PaymentDone message: {0}", msg));

    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to send SQS message", e);
    }
  }

  @Scheduled(fixedDelay = 2000)
  private void pollQueue() {
    log.info("Polling SQS for messages...");
    var messages = sqsClient.receiveMessage(r -> r
            .queueUrl(orderQueueUrl)
            .maxNumberOfMessages(5)
            .waitTimeSeconds(5))
        .messages();

    for (var msg : messages) {
      try {
        var orderCreatedMessage = objectMapper.readValue(msg.body(), OrderCreatedMessage.class);
        log.info(format("Successfully received OrderCreated message"));
        var userEmail = orderCreatedMessage.getUserEmail();

        Thread.sleep(2000); // imitate payment process
        orderServiceClient.confirmPayment(userEmail);
        var paymentMsg = PaymentDoneMessage.builder()
            .userEmail(userEmail)
            .build();
        publishPaymentMessage(paymentMsg);
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Failed to parse JSON", e);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }


    }
  }
}
