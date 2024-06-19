package com.oauth.ecom.entity.roles;

import java.util.Arrays;
import java.util.List;

public enum Role {

  MEMBER(Arrays.asList(Permissions.CANNOT_SEE)),
  ADMIN(Arrays.asList(Permissions.CAN_SEE));
  private List<Permissions> permission;

  Role(List<Permissions> Permission){
    this.permission = Permission;

  }
   public List<Permissions> getPermissions(){
    return permission;
  }
  public void setPermission(List<Permissions> permissions){
    this.permission = permissions;
  }

}
