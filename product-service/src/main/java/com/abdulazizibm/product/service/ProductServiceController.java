package com.abdulazizibm.product.service;

import static java.text.MessageFormat.format;

import com.abdulazizibm.product.service.data.Product;
import com.abdulazizibm.product.service.data.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductServiceController {

  private final ProductRepository productRepository;


  @GetMapping("/listAll")
  public ResponseEntity<List<Product>> listProducts() {
    List<Product> products = productRepository.findAll();
    return ResponseEntity.ok(products);

  }
  @GetMapping("/list")
  public ResponseEntity<JsonNode> listProduct(@RequestParam("name") String name) {
    Optional<Product> productOptional = productRepository.findByName(name);
    val jsonNode = JsonNodeFactory.instance.objectNode();

    if (productOptional.isEmpty()) {
      jsonNode.put("message", format("Product with name {0} not found", name));
      return ResponseEntity.status(404)
          .body(jsonNode);
    }
    val product = productOptional.get();
    jsonNode.put("name", product.getName());
    jsonNode.put("category", product.getCategory());
    jsonNode.put("price", product.getPrice());
    return ResponseEntity.ok(jsonNode);
  }

}
