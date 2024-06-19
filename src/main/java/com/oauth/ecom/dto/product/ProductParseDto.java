package com.oauth.ecom.dto.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper=false)
public class ProductParseDto {
  private Long productId;
  private long user;
  private int quantity;
  public ProductParseDto(long productId , long user , int quantity){
    this.productId = productId;
    this.user = user;
    this.quantity =  quantity;
  }
  @JsonCreator
    public ProductParseDto(
            @JsonProperty("productId") long productId,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("user") long user) {
        this.productId = productId;
        this.user = user;
        this.quantity = quantity;
    }
}
