package com.oauth.ecom.dto.product;

import java.util.List;

import com.oauth.ecom.entity.Products;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
  private Long id;
  private String name;
  private String description;
  private List<String> images;
  private Long category;
  private float originalPrice;
  private float price;
  private Long stock;
  private int returnPeriod;
  private String brand;
  private float rating;
  
  public ProductDto(Products products) {
    this.id = products.getId();
    this.name = products.getName();
    this.description = products.getDescription();
    this.images = products.getImages();
    this.category = products.getCategory().getId();
    this.originalPrice = products.getOriginalPrice();
    this.price = products.getPrice();
    this.stock = products.getStock();
    this.returnPeriod = products.getReturnPeriod();
    this.brand = products.getBrand();
    this.rating = products.getRating();
  }
}