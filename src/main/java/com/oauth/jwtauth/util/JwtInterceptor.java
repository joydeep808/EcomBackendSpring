package com.oauth.jwtauth.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JwtInterceptor {
  
  @Autowired
  private JwtUtil jwtUtil;
  public String getEmailFromJwt(HttpServletRequest httpServletRequest) throws Exception{
    Cookie[] cookies = httpServletRequest.getCookies();
    if (cookies.length == 0) {
      throw new Exception("Please login ");
    }
    String userEmail = "";
    String refreshToken = "";
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals("accessToken")) {
        userEmail = jwtUtil.extractEmail(cookie.getValue());
      }
      if (cookie.getName().equals("refreshToken")) {
        refreshToken = cookie.getValue(); 
      }
    }
    if (userEmail.equals("") && refreshToken.equals("") ) {
      throw new Exception("Access Token not found");
    }
    if (userEmail.equals("") && !refreshToken.equals("")) {
      return jwtUtil.extractEmail(refreshToken);
    }
    return userEmail;
  }

  public int getIdFromJwt(HttpServletRequest httpServletRequest) throws Exception{
    Cookie[] cookies = httpServletRequest.getCookies();
    if (cookies.length == 0) {
      throw new Exception("Please login ");
    }
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals("accessToken")) {
         return  jwtUtil.extractId(cookie.getValue());
      }
      if (cookie.getName().equals("refreshToken")) {
         
      return jwtUtil.extractId(cookie.getValue());

      }
    }
    return -1;
  }
}
