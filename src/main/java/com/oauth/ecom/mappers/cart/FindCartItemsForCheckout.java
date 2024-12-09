package com.oauth.ecom.mappers.cart;


public interface FindCartItemsForCheckout {
  Long getId();
  Long getProductId();
  int getQuantity();
  Long getProductPrice();
  int getProductQuantity();
  String getColor();

  
}