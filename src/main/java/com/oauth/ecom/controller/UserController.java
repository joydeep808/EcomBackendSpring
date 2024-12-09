package com.oauth.ecom.controller;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.oauth.ecom.dto.userdto.LoginDto;
import com.oauth.ecom.dto.userdto.UpdateDto;
import com.oauth.ecom.entity.Rating;
import com.oauth.ecom.entity.UserEntity;
import com.oauth.ecom.repository.UserRepo;
import com.oauth.ecom.services.redis.RedisService;
import com.oauth.ecom.services.user.UserAuthService;
import com.oauth.ecom.services.user.UserInfo;
import com.oauth.ecom.util.ReqRes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
  private UserInfo userInfo;
  private final UserRepo userRepo;
  private final UserAuthService authService;
  private final RedisService redisService;
  // @GetMapping("/")
  // public ResponseEntity<ReqRes> home(){
  // ReqRes response = new ReqRes();
  // response.setMessage("Welcome ");
  // response.setStatusCode(200);
  // return ResponseEntity.ok(response);
  // }

  @GetMapping("/cookies")
  public Cookie[] getClientCookies(HttpServletRequest request, HttpServletResponse response) {
    Cookie cookie = new Cookie("rabbitmq", "Hello");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(60);
    cookie.setSecure(true);
    response.addCookie(cookie);
    return request.getCookies();
  }

  @PostMapping("/register")
  public ResponseEntity<ReqRes<UserEntity>> createUser(@RequestBody @Valid UserEntity userEntity) {
    UserEntity foundUser = userRepo.findByEmail(userEntity.getEmail());
    if (foundUser != null) {
      return new ReqRes<UserEntity>().sendErrorMessage(400, "email already exist").sendResponseEntity();
    }
    return authService.signup(userEntity);

  }

  @PostMapping("/login")
  public ResponseEntity<ReqRes<UserEntity>> login(@RequestBody @Valid LoginDto loginDto,
      HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
    return authService.login(loginDto, httpServletRequest, httpServletResponse);
  }

  @GetMapping("/token")
  public ResponseEntity<ReqRes<String>> generateRefreshToken(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse) {
    return authService.getnewAccessToken(httpServletRequest, httpServletResponse);
  }

  @GetMapping("/user")
  public ResponseEntity<ReqRes<String>> user() {
    return userInfo.onlyUser();
  }

  @GetMapping("/info")
  public ResponseEntity<ReqRes<UserEntity>> getInfo(HttpServletRequest httpServletRequest) throws Exception {
    return userInfo.getOwnInfo(httpServletRequest);
  }

  @PutMapping("/update")
  public ResponseEntity<ReqRes<Object>> updateInfo(HttpServletRequest httpServletRequest,
      @RequestBody UpdateDto updateDto) throws Exception {
    return userInfo.updateInfo(httpServletRequest, updateDto);
  }

  @PutMapping("/avatar")
  public ResponseEntity<ReqRes<String>> setAvatar(HttpServletRequest httpServletRequest,
      @RequestParam("avatar") MultipartFile avatar) throws Exception {
    return userInfo.AddRemoveImage(httpServletRequest, avatar);
  }

  @GetMapping("/redis")
  public Object redis(@RequestParam("id") String id) throws Exception {
    Set<Object> value = redisService.getMap(id, Rating.class);
    Rating r = new Rating(Long.valueOf(32), Long.valueOf(32), 3, "Comment");
    if (value == null || value.size() == 0) {
      redisService.saveInSingleQuery(id, r, 20);
      return "Done";
    }
    return value;

  }

}
