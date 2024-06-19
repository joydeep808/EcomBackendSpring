package com.oauth.ecom.dto.cart;

import lombok.Data;

@Data
public class AddCartQuantityDto {
  private Long productId;
  private int quantity;

}
