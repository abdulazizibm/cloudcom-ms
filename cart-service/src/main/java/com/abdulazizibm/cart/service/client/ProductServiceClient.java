package com.abdulazizibm.cart.service.client;

import com.abdulazizibm.common.data.ProductDtoOut;
import com.abdulazizibm.common.data.ProductDtoIn;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", url = "${PRODUCT_SERVICE_URL}")
public interface ProductServiceClient {

  @GetMapping("/product/get")
  ProductDtoOut getProduct(@RequestParam("name") String name);

  @GetMapping("/product/getAll")
  List<ProductDtoOut> getAllProducts();

  @PutMapping("/product/s2s/get")
  List<ProductDtoOut> getProducts(@RequestBody List<ProductDtoIn> productDtoIns);




}
