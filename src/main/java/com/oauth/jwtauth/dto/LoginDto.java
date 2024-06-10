package com.oauth.jwtauth.dto;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
  private String email;
  @NotEmpty(message = "Password is required for login ")
  private String password;
}
