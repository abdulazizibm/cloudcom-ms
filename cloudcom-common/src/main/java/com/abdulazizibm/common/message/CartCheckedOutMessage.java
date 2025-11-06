package com.abdulazizibm.common.message;

import com.abdulazizibm.common.data.Product;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartCheckedOutMessage {
  private String userEmail;
  private List<Product> products;
  private double totalPrice;
  private Instant timestamp;

}
