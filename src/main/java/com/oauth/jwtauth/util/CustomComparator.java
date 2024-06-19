package com.oauth.jwtauth.util;

import java.util.Comparator;
import java.util.Objects;

import com.oauth.jwtauth.entity.Products;

public class CustomComparator implements Comparator<Products>{
  private final String wordToMatch;
  private final float maxPrice;

  public CustomComparator(String wordToMatch, float maxPrice) {
    this.wordToMatch = wordToMatch;
    this.maxPrice = maxPrice;
}


  @Override
  public int compare(Products o1, Products o2) {
    
    boolean isNameContains = o1.getName().contains(wordToMatch);
    if (wordToMatch != null) {
      if (isNameContains) return 1;
      else if(!isNameContains) return -1;
    }
    if (!Objects.equals(maxPrice, 0)) {
      if (o1.getPrice() < o2.getPrice() && o1.getPrice() <= maxPrice && o2.getPrice() <= maxPrice) {
        return -1;
      }
      else if(o1.getPrice() > o2.getPrice()  && o1.getPrice() <= maxPrice && o2.getPrice() <= maxPrice){
        return 1;
      }
    }
    return 0;
  }

}
