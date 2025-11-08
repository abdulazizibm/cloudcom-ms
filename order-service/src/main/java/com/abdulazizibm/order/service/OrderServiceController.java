package com.abdulazizibm.order.service;

import com.abdulazizibm.order.service.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderServiceController {

  private final OrderService orderService;

  @PutMapping("/confirm")
  public ResponseEntity<String> confirmPayment(@RequestParam("userEmail") String userEmail) {
    try {
      orderService.confirmPayment(userEmail);
    } catch (OrderNotFoundException e) {
      return ResponseEntity.status(404).body(e.getMessage());
    }
    return ResponseEntity.ok("Confirmed payment");
  }

}
