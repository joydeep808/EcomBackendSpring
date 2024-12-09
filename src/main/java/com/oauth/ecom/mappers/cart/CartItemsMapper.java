package com.oauth.ecom.mappers.cart;

public interface CartItemsMapper {
  Long getId();
  Long getProductId();
  int getQuantity();
  Long getProductPrice();
  int getProductQuantity();
  
}
