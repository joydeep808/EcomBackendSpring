package com.oauth.ecom.dto.product;

import java.util.List;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CreateProductDto {
  @NotNull(message = "Name is required ")
  private String name;
  @NotNull(message = "Description is required")
  private String description;
  @NotNull(message = "Please provide the return period")
  private int returnPeriod;
  private List<String> images;
  @NotNull(message = "CategoryId is required")
  private String categoryName;
  @NotNull(message = "Original price is required")
  private float originalPrice;
  @NotNull(message = "Price is required")
  private float price;
  @NotNull(message = "Color is required")
  private String color;
  @NotNull(message = "Stock should not be null")
  private Long stock;
  private String brand;
  private Integer rating;

}
