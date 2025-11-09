package com.abdulazizibm.product.service;

import com.abdulazizibm.common.data.ProductDtoIn;
import com.abdulazizibm.common.data.ProductDtoOut;
import com.abdulazizibm.product.service.data.ProductEntity;
import com.abdulazizibm.product.service.data.ProductRepository;
import com.abdulazizibm.product.service.exception.ProductDoesNotExistException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class ProductService {
  private final ProductRepository productRepository;

  public List<ProductDtoOut> getAll() {
    List<ProductEntity> products = productRepository.findAll();
    List<ProductDtoOut> dtos = new ArrayList<>();

    for(val product : products){
      val dto = new ProductDtoOut(product.getName(),product.getPrice());
      dtos.add(dto);
    }
    return dtos;

  }

  public ProductDtoOut getProduct(String productName) {
    Optional<ProductEntity> productOptional = productRepository.findByName(productName);

    if (productOptional.isEmpty()) {
      throw new ProductDoesNotExistException(productName);
    }
    val product = productOptional.get();
    return new ProductDtoOut(product.getName(), product.getPrice());
  }

  public List<ProductDtoOut> getProducts(List<ProductDtoIn> productDtoIns){
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
