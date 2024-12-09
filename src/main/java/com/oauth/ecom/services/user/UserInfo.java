package com.oauth.ecom.services.user;


import java.nio.file.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.oauth.ecom.dto.userdto.UpdateDto;
import com.oauth.ecom.entity.UserEntity;
import com.oauth.ecom.repository.UserRepo;
import com.oauth.ecom.util.JwtInterceptor;
import com.oauth.ecom.util.ReqRes;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserInfo {
  
  @Autowired
  private UserRepo userRepo;
  @Autowired
  private JwtInterceptor jwtInterceptor;

  public ResponseEntity<ReqRes<UserEntity>> getOwnInfo(HttpServletRequest httpServletRequest) throws Exception{
    ReqRes<UserEntity> response = new ReqRes<>();

     String email =  jwtInterceptor.getEmailFromJwt(httpServletRequest);
     
    if (email.equals(null)) {
      return response.sendErrorMessage(400 , "Please login").sendResponseEntity();
    }
    UserEntity user = userRepo.findByEmail(email);
    if (user.equals(null) || user == null ) {
       return response.sendErrorMessage(404,"User not found please login again ").sendResponseEntity();
    }
    else{
      return response.sendSuccessResponse(200 , "User details found" , user).sendResponseEntity();
    }
  }

  public ResponseEntity<ReqRes<Object>> updateInfo(HttpServletRequest httpServletRequest , UpdateDto updateDto)  throws Exception{
    ReqRes<Object> response = new ReqRes<Object>();
     final String email =  jwtInterceptor.getEmailFromJwt(httpServletRequest);
     if (email.equals(null)) {

      response.sendErrorMessage(401 , "Please Login");
      
     }
      UserEntity user = userRepo.findByEmail(email);
      if ( user == null || user.equals(null)) {
        return response.sendErrorMessage(404 , "User not found with this details ").sendResponseEntity();
      }
      else{
        if (updateDto.getUsername().equals(null) && updateDto.getEmail().equals(null)) {
          return response.sendErrorMessage(400 , "Update not possible please give me one value").sendResponseEntity();
          
        }
        if (!updateDto.getUsername().equals(null) || !updateDto.getUsername().isEmpty()) {
            user.setUsername(updateDto.getUsername());
        }
        if (!updateDto.getEmail().equals(null) || !updateDto.getEmail().isEmpty()) {
          user.setEmail(updateDto.getEmail());
        }
        userRepo.save(user);
        return response.sendSuccessResponse(201 , "User Update successfully done ").sendResponseEntity();
      }
      
  
  }
  public ResponseEntity<ReqRes<String>> onlyUser(){
    ReqRes <String>response = new ReqRes<>();
    return response.sendSuccessResponse(200  , "you are user").sendResponseEntity();
    

  }

  public ResponseEntity<ReqRes<String>> AddRemoveImage(HttpServletRequest httpServletRequest ,MultipartFile avatar ) throws Exception{
    ReqRes<String> response = new ReqRes<>();
    String uploadDir = "./Uploads";
    
  String filename = StringUtils.cleanPath(avatar.getOriginalFilename());
    if (filename.contains("..")) {
      return  response.sendErrorMessage(400 , "Please provide the valid image ").sendResponseEntity();
      
    }
    Path targetLocation = Paths.get(uploadDir).resolve(System.currentTimeMillis()+filename);
    Files.copy(avatar.getInputStream() , targetLocation);
    String email = jwtInterceptor.getEmailFromJwt(httpServletRequest);
    UserEntity user = userRepo.findByEmail(email);
    user.setAvatar("/Uploads"+System.currentTimeMillis()+avatar.getOriginalFilename());
    userRepo.save(user);
    return response.sendErrorMessage(200 , "Image upload successfully done ").sendResponseEntity();
    

  }
  // i have to implement email verification and also the reset password validation and other things also 
  
}
