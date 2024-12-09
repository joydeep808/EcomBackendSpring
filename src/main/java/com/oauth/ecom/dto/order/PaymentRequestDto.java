package com.oauth.ecom.dto.order;

import lombok.Data;

@Data
public class PaymentRequestDto {
  private int amount;
  private String currency;
  private String id;
  private String email;
}
