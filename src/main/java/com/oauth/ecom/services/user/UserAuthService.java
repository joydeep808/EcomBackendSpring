package com.oauth.ecom.services.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.oauth.ecom.dto.ReqRes;
import com.oauth.ecom.dto.userdto.LoginDto;
import com.oauth.ecom.dto.userdto.UserEmailSendDto;
import com.oauth.ecom.entity.Cart;
import com.oauth.ecom.entity.UserEntity;
import com.oauth.ecom.repository.CartRepo;
import com.oauth.ecom.repository.UserRepo;
import com.oauth.ecom.services.kafka.KafkaService;
import com.oauth.ecom.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Service
public class UserAuthService {
  @Autowired CartRepo cartRepo;
  @Autowired PasswordEncoder passwordEncoder;
  @Autowired UserRepo userRepo;
  @Autowired  JwtUtil jwtUtil;
  @Autowired KafkaService kafkaService;
  public ReqRes signup(UserEntity userEntity){
    ReqRes reqRes = new ReqRes();
    try {
      userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
      userEntity.setRole(userEntity.getRole());
      userEntity.setUsername(userEntity.getUsername());
      userEntity.setEmail(userEntity.getEmail());
     UserEntity user =  userRepo.save(userEntity);

     /// i will create cart By Default when the user is created
     Cart cart = new Cart();
     cart.setUser(userEntity);
     cartRepo.save(cart);
     reqRes.setUser(user);
    kafkaService.sendMessage("welcome_Email_Send", new UserEmailSendDto(user));
     reqRes.setStatusCode(200);
     reqRes.setMessage("User created successfully done");
    } catch (Exception e) {
      reqRes.setStatusCode(500);
    reqRes.setMessage("Failed to save user");
    reqRes.setError(e.getMessage());
    }
    return reqRes;
  }

  public ReqRes login (LoginDto loginDto , HttpServletRequest httpRequest , HttpServletResponse response) {
    ReqRes repsonse = new ReqRes();
    try{
    // authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
    UserEntity user = userRepo.findByEmail(loginDto.getEmail());

    if (user == null) {
      repsonse.setMessage("User not found with this email");
      repsonse.setStatusCode(404);
    }
    else {
     Boolean isPasswordValid =  passwordEncoder.matches(loginDto.getPassword() , user.getPassword());
     if (isPasswordValid.equals(false)) {
      repsonse.setMessage("Password not valid");
      repsonse.setStatusCode(400);
     }else{
        // SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      String token = jwtUtil.generateToken(user);
      String refreshToken = jwtUtil.generateRefreshToken(user);
      Cookie accessToken = new Cookie("accessToken" , token);
      Cookie setRefreshToken = new Cookie("refreshToken" , refreshToken);
      accessToken.setMaxAge(1 * 60 );
      accessToken.setPath("/");
      accessToken.setPath("/");
  // accessToken.setHttpOnly(true);
  // accessToken.setSecure(true);
  setRefreshToken.setPath("/");
  setRefreshToken.setMaxAge(24 * 60 * 60 * 60 );
  
  // setRefreshToken.setHttpOnly(true);
  // setRefreshToken.setSecure(true);
  response.addCookie(accessToken);

  response.addCookie(setRefreshToken);
  user.setRefreshToken(refreshToken);
  repsonse.setToken(token);
  repsonse.setRefreshToken(refreshToken);
  repsonse.setStatusCode(200);
  repsonse.setMessage("Login successfully done");
  userRepo.save(user);
     }
    }}
    catch (Exception e) {
      repsonse.setError(e.getMessage());
      repsonse.setMessage(e.getMessage());
      repsonse.setStatusCode(500);
    }
    return repsonse;
  }
  public ReqRes getnewAccessToken(HttpServletRequest httpServletRequest , HttpServletResponse httpServletResponse){
    ReqRes response = new ReqRes();
    Cookie[] cookies = httpServletRequest.getCookies();
    String refreshToken ="";
    if (cookies.length == 0) {
      response.setMessage("Cookies not found please login ");
      response.setStatusCode(500);
      return response;
    }
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals("refreshToken")) {
        refreshToken = cookie.getValue();
        break;
      }
    }
    if (refreshToken==null ||refreshToken.equals("") ) {
    response.setMessage("refresh token not found");
    response.setStatusCode(500);
    return response;
    }
   String email =  jwtUtil.extractEmail(refreshToken);
   UserEntity foundUser = userRepo.findByEmail(email);
   if (foundUser == null || foundUser.equals(null)) {
    // throw new Exception("User not found");
    response.setMessage("User not found with this token");
    response.setStatusCode(500);
    return response;
   }
   if (!foundUser.getRefreshToken().equals(refreshToken)) {
    response.setMessage("Please login again ");
    response.setStatusCode(500);
    // httpServletResponse.addCookie("refreshToken");
    Cookie expiredCookie = new Cookie("refreshToken", "");
    expiredCookie.setMaxAge(0);
    httpServletResponse.addCookie(expiredCookie);
    return response;
   }
   String token= jwtUtil.generateToken(foundUser);
   Cookie accessToken = new Cookie("accessToken", token);
   httpServletResponse.addCookie(accessToken);
   response.setMessage("Token generated successfully done!");
   response.setStatusCode(200);
   return response;
  }
}
