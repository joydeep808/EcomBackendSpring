package com.oauth.jwtauth.services.user;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.oauth.jwtauth.dto.ReqRes;
import com.oauth.jwtauth.dto.UpdateDto;
import com.oauth.jwtauth.entity.UserEntity;
import com.oauth.jwtauth.repository.UserRepo;
import com.oauth.jwtauth.util.JwtInterceptor;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserInfo {
  
  @Autowired
  private UserRepo userRepo;
  @Autowired
  private JwtInterceptor jwtInterceptor;

  public ReqRes getOwnInfo(HttpServletRequest httpServletRequest){
    ReqRes response = new ReqRes();

   try {
     String email =  jwtInterceptor.getEmailFromJwt(httpServletRequest);
     
    if (email.equals(null)) {
      response.setStatusCode(401);
      response.setMessage("Please login");
      return response;
    }
    UserEntity user = userRepo.findByEmail(email);
    if (user.equals(null) || user == null ) {
       response.setMessage("User not found please login again ");
       response.setStatusCode(404);
    }
    else{
      response.setMessage("User found");
      response.setStatusCode(200);
      response.setData(user);
      response.setIsSuccess(true);
    }
   } catch (Exception e) {
    response.setError(e.getMessage());
    response.setMessage("error from our side");
    response.setStatusCode(500);
   }
    return response;
  }

  public ReqRes updateInfo(HttpServletRequest httpServletRequest , UpdateDto updateDto){
    ReqRes response = new ReqRes();
  try {
     final String email =  jwtInterceptor.getEmailFromJwt(httpServletRequest);
     if (email.equals(null)) {
      response.setMessage("Please Login");
      response.setStatusCode(401);
      return response;
      
     }
      UserEntity user = userRepo.findByEmail(email);
      if ( user == null || user.equals(null)) {
        response.setMessage("User not found with this details ");
        response.setStatusCode(404);
      }
      else{
        if (updateDto.getUsername().equals(null) && updateDto.getEmail().equals(null)) {
          response.setMessage("Update not possible please give me one value");;
          response.setStatusCode(400);
          return response;
        }
        if (!updateDto.getUsername().equals(null) || !updateDto.getUsername().isEmpty()) {
            user.setUsername(updateDto.getUsername());
        }
        if (!updateDto.getEmail().equals(null) || !updateDto.getEmail().isEmpty()) {
          user.setEmail(updateDto.getEmail());
        }
        userRepo.save(user);
        response.setStatusCode(200);
        response.setMessage("User Update successfully done ");
      }
      
  } catch (Exception e) {
    response.setStatusCode(500);
    response.setError(e.getMessage());
    response.setMessage("We cannot reach our servers");
  }
  return response;
  }
  public ReqRes onlyUser(){
    ReqRes response = new ReqRes();
    response.setMessage("you are user ");
    response.setStatusCode(200);
    return response;

  }

  public ReqRes AddRemoveImage(HttpServletRequest httpServletRequest ,MultipartFile avatar ){
    ReqRes response = new ReqRes();
    String uploadDir = "./Uploads";
    
  String filename = StringUtils.cleanPath(avatar.getOriginalFilename());
  try {
    if (filename.contains("..")) {
      response.setMessage("Please provide the valid image ");
      response.setStatusCode(400);

      return response;
    }
    Path targetLocation = Paths.get(uploadDir).resolve(System.currentTimeMillis()+filename);
    Files.copy(avatar.getInputStream() , targetLocation);
    String email = jwtInterceptor.getEmailFromJwt(httpServletRequest);
    UserEntity user = userRepo.findByEmail(email);
    user.setAvatar("/Uploads"+System.currentTimeMillis()+avatar.getOriginalFilename());
    userRepo.save(user);
    response.setMessage("Image upload successfully done ");
    response.setStatusCode(200);
    return response;
  } catch (Exception e) {
    response.setMessage("We are not able to reach our servers");
    response.setError(e.getMessage());
    response.setStatusCode(500);
    return response;
  }
  }

  public ReqRes findUser(UserEntity userEntity){
    ReqRes res = new ReqRes();

   UserEntity user =  userRepo.findByUsernameAndEmail(userEntity.getUsername() , userEntity.getEmail());
    if (user == null || user.equals(null)) {
      res.setMessage("User not found");
    }
    else{
      res.setMessage("found");
    res.setUser(user);
    }
    return res;
  }
}
