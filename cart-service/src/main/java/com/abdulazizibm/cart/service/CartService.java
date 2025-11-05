package com.abdulazizibm.cart.service;


import com.abdulazizibm.cart.service.client.ProductServiceClient;
import com.abdulazizibm.cart.service.data.Cart;
import com.abdulazizibm.cart.service.data.CartProduct;
import com.abdulazizibm.cart.service.data.CartRepository;
import com.abdulazizibm.cart.service.exception.CartNotFoundException;
import com.abdulazizibm.cart.service.exception.ProductNotFoundException;
import com.abdulazizibm.common.data.ProductDtoIn;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CartService {

  private final CartRepository cartRepository;
  private final ProductServiceClient productServiceClient;

  protected List<CartProduct> get(String userEmail) {

    val cartOptional = cartRepository.findByUserEmail(userEmail);

    if (cartOptional.isEmpty()) {
      throw new CartNotFoundException(userEmail);
    }
    return cartOptional.get()
        .getProducts();

  }

  protected String add(List<ProductDtoIn> productDtoIns, String userEmail){
    val products = productServiceClient.getProducts(productDtoIns);
    var cartOptional = cartRepository.findByUserEmail(userEmail);

    if (cartOptional.isEmpty()) {
      val newCart = Cart.builder()
          .userEmail(userEmail)
          .products(new ArrayList<>())
          .build();
      cartOptional = Optional.of(newCart);
    }
    val cart = cartOptional.get();

    var newCartProducts = new ArrayList<CartProduct>();

    for (int i = 0; i < products.size(); i++) {
      var cartProduct = new CartProduct();
      cartProduct.setName(products.get(i).name());
      cartProduct.setPrice(products.get(i).price());
      cartProduct.setQuantity(productDtoIns.get(i).quantity());
      newCartProducts.add(cartProduct);
    }
    cart.getProducts().addAll(newCartProducts);
    cartRepository.save(cart);

    return "Products successfully added to cart";
  }

  @Transactional
  protected String remove(String productName, String userEmail){
    var cartOptional = cartRepository.findByUserEmail(userEmail);

    if (cartOptional.isEmpty()) {
      throw new CartNotFoundException(userEmail);
    }
    var cart = cartOptional.get();

    var productOptional = cart.getProducts()
        .stream()
        .filter(p -> p.getName()
            .equals(productName))
        .findAny();

    if (productOptional.isEmpty()) {
      throw new ProductNotFoundException(productName, userEmail);
    }
    var productToRemove = productOptional.get();
    var quantity = productToRemove.getQuantity();

    cart.getProducts()
        .remove(productToRemove);

    if (quantity > 1) {
      productToRemove.setQuantity(quantity - 1);
      cart.getProducts()
          .add(productToRemove);
    }
    cartRepository.save(cart);
    return "Product successfully removed from cart";
  }

}
