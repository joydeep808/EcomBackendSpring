package com.oauth.jwtauth.dto.cartitems;

import com.oauth.jwtauth.entity.CartItems;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class CreateCartItemsDto extends CartItems {
  private Long productId;
}
