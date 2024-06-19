package com.oauth.jwtauth.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.oauth.jwtauth.dto.ReqRes;
import com.oauth.jwtauth.dto.userdto.LoginDto;
import com.oauth.jwtauth.dto.userdto.UpdateDto;
import com.oauth.jwtauth.entity.UserEntity;
import com.oauth.jwtauth.repository.UserRepo;
import com.oauth.jwtauth.services.user.AuthService;
import com.oauth.jwtauth.services.user.UserInfo;

import jakarta.servlet.http.*;
import jakarta.validation.Valid;

@Controller
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
  @Autowired(required = true)
  private UserInfo userInfo;
  @Autowired
  private UserRepo userRepo;
  @Autowired
  private AuthService authService;
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
    Optional<UserEntity> foundUser = Optional.ofNullable(userRepo.findByEmail(userEntity.getEmail()));
    System.out.println(foundUser);
    if (!foundUser.isEmpty()) {
      response.setMessage("Email already exist");
      response.setStatusCode(400);
      return ResponseEntity.ok(response);
    }
    return ResponseEntity.ok(authService.signup(userEntity));
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

