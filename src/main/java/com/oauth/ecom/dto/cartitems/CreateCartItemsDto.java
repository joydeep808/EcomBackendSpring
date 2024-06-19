package com.oauth.ecom.dto.cartitems;

import com.oauth.ecom.entity.CartItems;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class CreateCartItemsDto extends CartItems {
  private Long productId;
  
}
