package com.abdulazizibm.cart.service;


import static java.text.MessageFormat.format;

import com.abdulazizibm.cart.service.client.ProductServiceClient;
import com.abdulazizibm.cart.service.data.Cart;
import com.abdulazizibm.common.message.CartCheckedOutMessage;
import com.abdulazizibm.common.data.Product;
import com.abdulazizibm.cart.service.data.CartRepository;
import com.abdulazizibm.cart.service.exception.CartNotFoundException;
import com.abdulazizibm.cart.service.exception.ProductNotFoundException;
import com.abdulazizibm.common.data.ProductDtoIn;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.QueueDoesNotExistException;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class CartService {

  private final CartRepository cartRepository;
  private final ProductServiceClient productServiceClient;
  private final SqsClient sqsClient;
  private final ObjectMapper objectMapper;

  @Value("${aws.sqs.cart.queue.name}")
  private String queueName;
  private String queueUrl;

  @PostConstruct
  void init() {
    try {
      this.queueUrl =
          sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                  .queueName(queueName)
                  .build())
                  .queueUrl();
    } catch (QueueDoesNotExistException e) {
      val queue = sqsClient.createQueue(CreateQueueRequest.builder()
          .queueName(queueName)
          .build());
      this.queueUrl = queue.queueUrl();
    }
  }

  protected List<Product> get(String userEmail) {
    val cartOptional = cartRepository.findByUserEmail(userEmail);
    if (cartOptional.isEmpty()) {
      throw new CartNotFoundException(userEmail);
    }
    return cartOptional.get().getProducts();

  }

  protected String add(List<ProductDtoIn> productDtoIns, String userEmail) {
    val products = productServiceClient.getProducts(productDtoIns);
    var cartOptional = cartRepository.findByUserEmail(userEmail);

    if (cartOptional.isEmpty()) {
      val newCart = Cart.builder()
          .userEmail(userEmail)
          .products(new ArrayList<>())
          .build();
      cartOptional = Optional.of(newCart);
    }
    val cart = cartOptional.get();

    var newCartProducts = new ArrayList<Product>();

    for (int i = 0; i < products.size(); i++) {
      Product cartProduct = new Product();
      cartProduct.setName(products.get(i).name());
      cartProduct.setPrice(products.get(i).price());
      cartProduct.setQuantity(productDtoIns.get(i).quantity());
      newCartProducts.add(cartProduct);
    }
    cart.getProducts().addAll(newCartProducts);
    cartRepository.save(cart);

    return "Products successfully added to cart";
  }

  @Transactional
  protected String remove(String productName, String userEmail) {
    var cartOptional = cartRepository.findByUserEmail(userEmail);

    if (cartOptional.isEmpty()) {
      throw new CartNotFoundException(userEmail);
    }
    var cart = cartOptional.get();

    var productOptional = cart.getProducts()
        .stream()
        .filter(p -> p.getName().equals(productName))
        .findAny();

    if (productOptional.isEmpty()) {
      throw new ProductNotFoundException(productName, userEmail);
    }
    var productToRemove = productOptional.get();
    var quantity = productToRemove.getQuantity();

    cart.getProducts().remove(productToRemove);

    if (quantity > 1) {
      productToRemove.setQuantity(quantity - 1);
      cart.getProducts().add(productToRemove);
    }
    cartRepository.save(cart);
    return "Product successfully removed from cart";
  }

  public void publishToSqsQueue(CartCheckedOutMessage message) {
    try {
      String msg = objectMapper.writeValueAsString(message);

      val msgRequest = SendMessageRequest.builder()
          .queueUrl(queueUrl)
          .messageBody(msg)
          .build();
      sqsClient.sendMessage(msgRequest);
      log.info(format("Successfully send CartCheckedOut message: {0}", msg));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to send SQS message", e);
    }
  }

}
