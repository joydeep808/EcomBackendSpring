package com.oauth.jwtauth.services;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Optionals;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.oauth.jwtauth.dto.LoginDto;
import com.oauth.jwtauth.dto.ReqRes;
import com.oauth.jwtauth.entity.UserEntity;
import com.oauth.jwtauth.repository.UserRepo;
import com.oauth.jwtauth.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private UserRepo userRepo;
  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired 
  private JwtUtil jwtUtil;
  public ReqRes signup(UserEntity userEntity){
    ReqRes reqRes = new ReqRes();
    try {
      userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
      userEntity.setRole(userEntity.getRole());
      userEntity.setUsername(userEntity.getUsername());
      userEntity.setEmail(userEntity.getEmail());
     UserEntity user =  userRepo.save(userEntity);
     reqRes.setUser(user);
     reqRes.setStatusCode(200);
     reqRes.setMessage("User created successfully done");

    } catch (Exception e) {
      reqRes.setStatusCode(500);
    reqRes.setMessage("Failed to save user");
    reqRes.setError(e.getMessage());
    }
    
    return reqRes;
  }

  public ReqRes login (LoginDto loginDto , HttpServletRequest httpRequest , HttpServletResponse response) throws Exception{
    ReqRes repsonse = new ReqRes();
    try{
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
    UserEntity user = userRepo.findByUsername(loginDto.getUsername());
    System.out.println(user);
    if (user == null) {
      repsonse.setMessage("User not found with this email");
      repsonse.setStatusCode(404);
      
    }
    else {
      String token = jwtUtil.generateToken(user);
      String refreshToken = jwtUtil.generateRefreshToken(new HashMap<>(), user);
      Cookie accessToken = new Cookie("accessToken" , token);
      Cookie setRefreshToken = new Cookie("refreshToken" , refreshToken);
      accessToken.setMaxAge(3600);
  accessToken.setHttpOnly(true);
  accessToken.setSecure(true);
  setRefreshToken.setMaxAge(3600 * 24 * 60);
  setRefreshToken.setHttpOnly(true);
  setRefreshToken.setSecure(true);
response.addCookie(accessToken);
response.addCookie(setRefreshToken);
  user.setRefreshToken(refreshToken);
  repsonse.setToken(token);
  repsonse.setRefreshToken(refreshToken);
  repsonse.setStatusCode(200);
  repsonse.setMessage("Login successfully done");
  userRepo.save(user);

    }}
    catch (Exception e) {
      repsonse.setError(e.getMessage());
      repsonse.setMessage(e.getMessage());
      repsonse.setStatusCode(500);
    }
    return repsonse;
  }

}
