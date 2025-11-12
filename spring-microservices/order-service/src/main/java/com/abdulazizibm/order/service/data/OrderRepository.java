package com.abdulazizibm.order.service.data;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

public interface OrderRepository extends JpaRepository<Order, Long> {
  Optional<Order> findByUserEmailAndStatus(@RequestParam("userEmail") String userEmail, @RequestParam("status") OrderStatus status);

}
