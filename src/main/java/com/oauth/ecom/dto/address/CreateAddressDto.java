package com.oauth.ecom.dto.address;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class CreateAddressDto {
  private Long  id;
@NotNull(message="Full address is required")
  private String fullAddress;
  @NotNull(message = "State is required")
  private String state;
  @NotNull(message = "Phone is required")
  // @Size( min=10, max = 10 ,message = "PinCode should be 6 digits")
  private Long phone;
  @NotNull(message = "Pincode is required")
  // @Size( min=6 , max = 6 ,message = "PinCode should be 6 digits")
  private int pincode;
}
