package com.abdulazizibm.product.service;

import com.abdulazizibm.common.data.ProductDtoOut;
import com.abdulazizibm.common.data.ProductDtoIn;
import com.abdulazizibm.product.service.exception.ProductDoesNotExistException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductServiceController {

  private final ProductService productService;

  @GetMapping("/getAll")
  public ResponseEntity<List<ProductDtoOut>> listProducts() {
    var products = productService.getAll();
    return ResponseEntity.ok(products);

  }
  @GetMapping("/get")
  public ResponseEntity<ProductDtoOut> listProduct(@RequestParam("name") String productName) {
    try{
      var productDto = productService.getProduct(productName);
      return ResponseEntity.ok(productDto);
    } catch(ProductDoesNotExistException e){
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/s2s/get")
  public List<ProductDtoOut> getProducts(@RequestBody List<ProductDtoIn> productDtos){
    return productService.getProducts(productDtos);

  }

}
