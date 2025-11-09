package com.abdulazizibm.user.communication.service;


import static java.text.MessageFormat.format;

import com.abdulazizibm.common.message.OrderCreatedMessage;
import com.abdulazizibm.user.communication.service.dto.LoginRequest;
import com.abdulazizibm.user.communication.service.dto.RegisterRequest;
import com.abdulazizibm.user.communication.service.exception.IncorrectPasswordException;
import com.abdulazizibm.user.communication.service.exception.UserAlreadyExistsException;
import com.abdulazizibm.user.communication.service.exception.UserNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.QueueDoesNotExistException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class UserComServiceController {

  private final UserComService userComService;
  private final SqsClient sqsClient;
  private final ObjectMapper objectMapper;

  @Value("${aws.sqs.payment.queue.name}")
  private String paymentQueue;
  private String paymentQueueUrl;

  @PostConstruct
  void init() {
    try {
      paymentQueueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
              .queueName(paymentQueue)
              .build())
          .queueUrl();
    } catch (QueueDoesNotExistException e) {
      val queue = sqsClient.createQueue(CreateQueueRequest.builder()
          .queueName(paymentQueue)
          .build());
      paymentQueueUrl = queue.queueUrl();
    }
  }

  @Scheduled(fixedDelay = 2000)
  private void pollQueue() {
    log.info("Polling SQS for messages...");
    var messages = sqsClient.receiveMessage(r -> r
            .queueUrl(paymentQueueUrl)
            .maxNumberOfMessages(5)
            .waitTimeSeconds(5))
            .messages();

    for (var msg : messages) {
      try {
        var orderCreatedMessage = objectMapper.readValue(msg.body(), OrderCreatedMessage.class);
        log.info(format("Successfully received PaymentDone message"));
        var userEmail = orderCreatedMessage.getUserEmail();
        log.info(format("Sending confirmation email to {0}", userEmail));

      } catch (JsonProcessingException e) {
        throw new RuntimeException("Failed to parse JSON", e);
      }
    }
  }

  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
    try {
      val response = userComService.register(request);
      return ResponseEntity.ok(response);

    } catch (UserAlreadyExistsException e) {
      return ResponseEntity.badRequest()
          .body(e.getMessage());
    }
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginRequest request) {
    try {
      val jwtToken = userComService.login(request);
      return ResponseEntity.ok(jwtToken);

    } catch (UserNotFoundException e) {
      return ResponseEntity.status(404)
          .body(e.getMessage());
    } catch (IncorrectPasswordException e) {
      return ResponseEntity.status(401)
          .body(e.getMessage());
    }

  }

}
