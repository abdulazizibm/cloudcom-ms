package com.abdulazizibm.cart.service.data;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart, Long> {
  Optional<Cart> findByUserEmail(@Param("userEmail") String userEmail);

}
