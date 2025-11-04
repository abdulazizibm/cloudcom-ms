package com.abdulazizibm.product.service;

import com.abdulazizibm.common.data.ProductDtoOut;
import com.abdulazizibm.common.data.ProductDtoIn;
import com.abdulazizibm.product.service.data.Product;
import com.abdulazizibm.product.service.data.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
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

  private final ProductRepository productRepository;


  @GetMapping("/getAll")
  public ResponseEntity<List<ProductDtoOut>> listProducts() {
    List<Product> products = productRepository.findAll();
    List<ProductDtoOut> dtos = new ArrayList<>();

    for(val product : products){
      val dto = new ProductDtoOut(product.getName(),product.getPrice());
      dtos.add(dto);
    }
    return ResponseEntity.ok(dtos);

  }
  @GetMapping("/get")
  public ResponseEntity<ProductDtoOut> listProduct(@RequestParam("name") String name) {
    Optional<Product> productOptional = productRepository.findByName(name);

    if (productOptional.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    val product = productOptional.get();
    val dto = new ProductDtoOut(product.getName(), product.getPrice());
    return ResponseEntity.ok(dto);
  }

  @PutMapping("/s2s/get")
  public List<ProductDtoOut> getProducts(@RequestBody List<ProductDtoIn> productDtoIns){
    val dtos = new ArrayList<ProductDtoOut>();

    for(val productName : productDtoIns) {
      var optionalProduct = productRepository.findByName(productName.name());
      if(optionalProduct.isEmpty()){
        continue;
      }
      var product = optionalProduct.get();
      var dto = new ProductDtoOut(product.getName(), product.getPrice());
      dtos.add(dto);
    }
    return dtos;

  }

}
