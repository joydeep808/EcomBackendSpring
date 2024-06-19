package com.oauth.ecom.dto.product;

import com.oauth.ecom.entity.Products;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper=false)
public class CreateProductDto  extends Products{
  
  @NotNull(message = "CategoryId is required")
  private int categoryId;
}
