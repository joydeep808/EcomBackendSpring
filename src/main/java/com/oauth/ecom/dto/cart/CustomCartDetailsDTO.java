package com.oauth.ecom.dto.cart;

public interface CustomCartDetailsDTO {
   Long cartId();
   Long userId();
   String userName();
   Long cartItemId();
   Long productId();
   String productName();
   String couponCode();
}
