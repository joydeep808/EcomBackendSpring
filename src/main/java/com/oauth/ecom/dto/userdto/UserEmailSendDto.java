package com.oauth.ecom.dto.userdto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oauth.ecom.entity.UserEntity;

import lombok.Data;

@Data
public class UserEmailSendDto {
  
  private String name;
  private String email;
  public UserEmailSendDto(UserEntity user){
    this.name = user.getName();
    this.email = user.getEmail();
  }

  @JsonCreator
  public UserEmailSendDto (@JsonProperty("name")String name , @JsonProperty("email") String email){
    this.email = email;
    this.name = name;
  }
}
