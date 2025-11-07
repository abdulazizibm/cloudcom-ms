package com.abdulazizibm.common.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderCreated {
  private Long id;
  private double totalPrice;

}
