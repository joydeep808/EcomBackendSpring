package com.oauth.ecom.mappers;

import java.util.List;

public interface ProductMapper {
  Long getId();
  String getName();
  String getDescription();
  List<String> getImages();
  Long getCategory();
  float getOriginalPrice();
  float getPrice();
  Long getStock();
  int getReturnPeriod();
  String getBrand();
  float getRating();
  String getCreatedAt();

}
