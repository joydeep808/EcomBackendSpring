package com.oauth.jwtauth.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.oauth.jwtauth.dto.LoginDto;
import com.oauth.jwtauth.dto.ReqRes;
import com.oauth.jwtauth.entity.UserEntity;
import com.oauth.jwtauth.repository.UserRepo;
import com.oauth.jwtauth.services.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RestController

public class UserController {
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
    System.out.println(userEntity);
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
  public ResponseEntity<ReqRes> login(@RequestBody @Valid LoginDto loginDto, HttpServletRequest httpServletRequest , HttpServletResponse response ) throws Exception{
    return ResponseEntity.ok(authService.login(loginDto, httpServletRequest, response));
  }
  @GetMapping("/private")
  public ResponseEntity<ReqRes> privateC(){
    ReqRes res = new ReqRes();
    res.setMessage("Private page auth");
    res.setStatusCode(200);;
    return ResponseEntity.ok(res);
  }
}
