package com.oauth.ecom.dto.product;

import java.util.List;

import lombok.Data;

@Data
public class ProductDto {
  private String name;
  private String description;
  private List<String> images;
  private Long categoryId;
  private Double originalPrice;
  private Double price;
  private Long stock;
  private String seller;
  
}
