package com.abdulazizibm.cart.service.data;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class CartProduct {

  private String name;
  private int quantity;
  private double price;

}
