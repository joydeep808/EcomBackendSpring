package com.oauth.ecom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.oauth.ecom.dto.userdto.LoginDto;
import com.oauth.ecom.dto.userdto.UpdateDto;
import com.oauth.ecom.entity.UserEntity;
import com.oauth.ecom.repository.UserRepo;
import com.oauth.ecom.services.user.UserAuthService;
import com.oauth.ecom.services.user.UserInfo;
import com.oauth.ecom.util.ReqRes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
  @Autowired(required = true) UserInfo userInfo;
  @Autowired UserRepo userRepo;
  @Autowired UserAuthService authService;
  @GetMapping("/")
  public ResponseEntity<ReqRes> home(){
    ReqRes response = new ReqRes();
    response.setMessage("Welcome ");
    response.setStatusCode(200);
    return ResponseEntity.ok(response);
  }
  @PostMapping("/register")
  public ResponseEntity<ReqRes> createUser(@RequestBody @Valid UserEntity userEntity){
    ReqRes response = new ReqRes();
    UserEntity foundUser = userRepo.findByEmail(userEntity.getEmail());
    if (foundUser != null) {
      response.setMessage("Email already exist");
      response.setStatusCode(400);
      return ResponseEntity.status(400).body(response);
    }
    return ResponseEntity.status(200).body(authService.signup(userEntity));
  }
  @PostMapping("/login")
  public ResponseEntity<ReqRes> login(@RequestBody @Valid LoginDto loginDto, HttpServletRequest httpServletRequest , HttpServletResponse httpServletResponse) throws Exception{
    ReqRes response = authService.login(loginDto, httpServletRequest, httpServletResponse);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
  @GetMapping("/token")
  public ResponseEntity<ReqRes> generateRefreshToken(HttpServletRequest httpServletRequest , HttpServletResponse httpServletResponse ){
    ReqRes response =authService.getnewAccessToken(httpServletRequest , httpServletResponse);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
  
  @GetMapping("/user")
  public ResponseEntity<ReqRes> user(){
    ReqRes response = userInfo.onlyUser();
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
  @GetMapping("/info")
  public ResponseEntity<ReqRes> getInfo(HttpServletRequest httpServletRequest){
    ReqRes response =userInfo.getOwnInfo(httpServletRequest);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
  @PutMapping("/update")
  public  ResponseEntity<ReqRes> updateInfo(HttpServletRequest httpServletRequest ,@RequestBody UpdateDto updateDto){
    ReqRes response = userInfo.updateInfo(httpServletRequest, updateDto);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
  @PutMapping("/avatar")
  public ResponseEntity<ReqRes> setAvatar(HttpServletRequest httpServletRequest, @RequestParam("avatar") MultipartFile avatar){
    ReqRes response =userInfo.AddRemoveImage(httpServletRequest, avatar);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
 
  
}

