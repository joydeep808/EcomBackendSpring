package com.oauth.ecom.dto.userdto;

import lombok.Data;

@Data
public class CustomUserDto {
  private String username;
    private String avatar;
    private String role;
    private String status;
    private String email;

    public CustomUserDto(String username, String avatar, String role, String status , String email) {
        this.username = username;
        this.avatar = avatar;
        this.role = role;
        this.status = status;
        this.email  = email;
    }
}
