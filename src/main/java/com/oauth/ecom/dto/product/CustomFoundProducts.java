package com.oauth.ecom.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomFoundProducts {
   private String name;
   private float price;
   private Long id;
   private String description;
   private int stock;
   
}
