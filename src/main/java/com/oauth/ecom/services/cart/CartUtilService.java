package com.oauth.ecom.services.cart;

import java.util.List;

import org.springframework.stereotype.Component;

import com.oauth.ecom.entity.Cart;
import com.oauth.ecom.repository.CartItemsRepo;
import com.oauth.ecom.repository.CartRepo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartUtilService {
  private final CartRepo cartRepo;
  private final CartItemsRepo cartItemsRepo;

  public void RemoveCartProducts(Cart cart, List<Long> cartItemsId) {
    cart.setCartTotal(0);
    cart.setCouponCode(null);
    cart.setDiscountCartTotal(0);
    cartRepo.save(cart);
    cartItemsRepo.deleteCartItems(cart.getId());
  }

}
