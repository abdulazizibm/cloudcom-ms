package com.abdulazizibm.product.service;

import com.abdulazizibm.product.service.data.ProductEntity;
import com.abdulazizibm.product.service.data.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductImporter implements CommandLineRunner {

  private final ProductRepository productRepository;

  @Override
  public void run(String... args) throws IOException {

    if (!productRepository.findAll().isEmpty()) {
      return;
    }

    InputStream inputStream = getClass().getClassLoader()
        .getResourceAsStream("products.json");

    if (inputStream == null) {
      throw new FileNotFoundException("products.json not found in classpath");
    }

    ObjectMapper objectMapper = new ObjectMapper();

    List<ProductEntity> products = objectMapper.readValue(inputStream,
        new TypeReference<>() {
        });

    productRepository.saveAll(products);
    log.info("Products successfully imported into a database");

  }
}
