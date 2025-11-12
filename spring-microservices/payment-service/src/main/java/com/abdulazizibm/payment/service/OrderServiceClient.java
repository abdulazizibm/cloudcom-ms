package com.abdulazizibm.payment.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service", url = "${ORDER_SERVICE_URL}")
public interface OrderServiceClient {

  @PutMapping("/order/confirm")
  void confirmPayment(@RequestParam("userEmail") String userEmail);

}
