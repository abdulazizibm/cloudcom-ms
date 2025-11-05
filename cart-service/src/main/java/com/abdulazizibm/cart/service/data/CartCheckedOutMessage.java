package com.abdulazizibm.cart.service.data;

import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartCheckedOutMessage {
  private String userEmail;
  private List<CartProduct> products;
  private double totalPrice;
  private Instant timestamp;


}
