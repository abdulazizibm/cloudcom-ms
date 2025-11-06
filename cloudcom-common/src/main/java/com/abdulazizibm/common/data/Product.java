package com.abdulazizibm.common.data;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Product {

  private String name;
  private int quantity;
  private double price;

}
