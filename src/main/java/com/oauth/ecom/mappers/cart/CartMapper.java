package com.oauth.ecom.mappers.cart;

import java.util.List;

import com.oauth.ecom.entity.CartItems;
import com.oauth.ecom.entity.CouponCode;

public interface CartMapper {
  List<CartItems> getCartItems();
  float getCartTotal();
  CouponCode getCouponCode();
  float getDiscountCartTotal();
}
