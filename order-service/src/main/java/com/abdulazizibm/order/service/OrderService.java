package com.abdulazizibm.order.service;

import static java.text.MessageFormat.format;

import com.abdulazizibm.common.message.CartCheckedOutMessage;
import com.abdulazizibm.common.data.Product;
import com.abdulazizibm.common.message.OrderCreatedMessage;
import com.abdulazizibm.order.service.data.Order;
import com.abdulazizibm.order.service.data.OrderRepository;
import com.abdulazizibm.order.service.data.OrderStatus;
import com.abdulazizibm.order.service.exception.OrderNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.util.List;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

  private final SqsClient sqsClient;
  private final ObjectMapper objectMapper;
  private final OrderRepository orderRepository;

  @Value("${aws.sqs.cart.queue.name}")
  private String cartQueue;
  private String cartQueueUrl;
  @Value("${aws.sqs.order.queue.name}")
  private String orderQueue;
  private String orderQueueUrl;

  @PostConstruct
  void init() {
    this.cartQueueUrl = getCartQueueUrl();
    this.orderQueueUrl = getOrderQueueUrl();
  }

  private String getCartQueueUrl() {
    try {
      return sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
              .queueName(cartQueue)
              .build())
          .queueUrl();
    } catch (QueueDoesNotExistException e) {
      val queue = sqsClient.createQueue(CreateQueueRequest.builder()
          .queueName(cartQueue)
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

  @Scheduled(fixedDelay = 2000)
  private void pollQueue() {
    log.info("Polling SQS for messages...");
    var messages = sqsClient.receiveMessage(r -> r
            .queueUrl(cartQueueUrl)
            .maxNumberOfMessages(5)
            .waitTimeSeconds(5)
        )
        .messages();

    for (var msg : messages) {
      try {
        var cartCheckedOutMessage = objectMapper.readValue(msg.body(),
            CartCheckedOutMessage.class);

        val userEmail = cartCheckedOutMessage.getUserEmail();
        val totalPrice = cartCheckedOutMessage.getTotalPrice();
        List<Product> products = cartCheckedOutMessage.getProducts();

        log.info(format("Successfully received CartCheckedOut message for user {0}", userEmail));

        val order = Order.builder()
            .userEmail(userEmail)
            .orderedProducts(products)
            .status(OrderStatus.PENDING) // set PENDING until successful payment
            .build();

        orderRepository.save(order);

        // Delete message from queue once processed
        sqsClient.deleteMessage(r -> r.queueUrl(cartQueueUrl)
            .receiptHandle(msg.receiptHandle()));

        val orderCreatedMessage = OrderCreatedMessage.builder()
            .id(order.getId())
            .totalPrice(totalPrice)
            .userEmail(userEmail)
            .build();

        publishOrderCreated(orderCreatedMessage);


      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void publishOrderCreated(OrderCreatedMessage message) {
    try {
      String msg = objectMapper.writeValueAsString(message);

      val msgRequest = SendMessageRequest.builder()
          .queueUrl(orderQueueUrl)
          .messageBody(msg)
          .build();
      sqsClient.sendMessage(msgRequest);
      log.info(format("Successfully send OrderCreated message: {0}", msg));

    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to send SQS message", e);
    }
  }

  public void confirmPayment(String userEmail){
    var orderOptional = orderRepository.findByUserEmail(userEmail);

    if(orderOptional.isEmpty()){
      throw new OrderNotFoundException(userEmail);
    }
    var order = orderOptional.get();
    order.setStatus(OrderStatus.PAID);
    log.info("Set order status to PAID");
    orderRepository.save(order);
  }


}
