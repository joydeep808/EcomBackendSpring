package com.oauth.jwtauth.dto.product;

import java.util.List;

import com.oauth.jwtauth.entity.Category;

import lombok.Data;

@Data
public class UpdateProductDto {
  private Long id;
  private String name;
  private float price;
  private Long stock;
  private String color;
  private Category category;
  private String description;
  private int returnPeriod;
  private float originalPrice;
  private List<String> images;

}
