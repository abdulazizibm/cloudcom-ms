package com.abdulazizibm.cart.service;

import com.abdulazizibm.common.data.Product;
import com.abdulazizibm.cart.service.exception.CartNotFoundException;
import com.abdulazizibm.cart.service.exception.ProductNotFoundException;
import com.abdulazizibm.common.data.ProductDtoIn;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class CartServiceController {

  private final CartService cartService;
  private final ObjectMapper objectMapper;

  @GetMapping("/get")
  public ResponseEntity<JsonNode> getCart() {
    val auth = SecurityContextHolder.getContext()
        .getAuthentication();
    val userEmail = auth.getName();

    List<Product> cartProducts;
    try {
      cartProducts = cartService.get(userEmail);
    } catch (CartNotFoundException e) {
      val jsonNode = objectMapper.valueToTree(e.getMessage());
      return ResponseEntity.status(404)
          .body(jsonNode);
    }
    val jsonNode = objectMapper.valueToTree(cartProducts);
    return ResponseEntity.ok(jsonNode);

  }

  @PutMapping("/add")
  public ResponseEntity<String> addToCart(@RequestBody List<ProductDtoIn> productDtoIns) {
    val auth = SecurityContextHolder.getContext()
        .getAuthentication();
    val userEmail = auth.getName();
    val result = cartService.add(productDtoIns, userEmail);

    return ResponseEntity.ok(result);

  }

  @PutMapping("/remove")
  public ResponseEntity<String> removeFromCart(@RequestParam("name") String name) {
    val auth = SecurityContextHolder.getContext().getAuthentication();
    val userEmail = auth.getName();

    String result;
    try {
      result = cartService.remove(name, userEmail);
    } catch (CartNotFoundException | ProductNotFoundException e) {
      return ResponseEntity.status(404)
          .body(e.getMessage());
    }
    return ResponseEntity.ok(result);

  }

  @PostMapping("/checkout")
  public ResponseEntity<String> checkOut() {
    val auth = SecurityContextHolder.getContext().getAuthentication();
    val userEmail = auth.getName();
    var checkedOutProducts = cartService.checkOut(userEmail);

    cartService.removeProducts(checkedOutProducts, userEmail);
    return ResponseEntity.ok("Cart successfully checked out");

  }


}
