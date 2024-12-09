package com.oauth.ecom.services.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.oauth.ecom.dto.userdto.LoginDto;
import com.oauth.ecom.entity.Cart;
import com.oauth.ecom.entity.UserEntity;
import com.oauth.ecom.repository.CartRepo;
import com.oauth.ecom.repository.UserRepo;
// import com.oauth.ecom.services.kafka.KafkaService;
import com.oauth.ecom.util.JwtUtil;
import com.oauth.ecom.util.ReqRes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserAuthService {
  @Autowired
  CartRepo cartRepo;
  @Autowired
  PasswordEncoder passwordEncoder;
  @Autowired
  UserRepo userRepo;
  @Autowired
  JwtUtil jwtUtil;

  // @Autowired KafkaService kafkaService;
  public ResponseEntity<ReqRes<UserEntity>> signup(UserEntity userEntity) {
    ReqRes<UserEntity> reqRes = new ReqRes<UserEntity>();
      userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
      userEntity.setRole(userEntity.getRole());
      userEntity.setUsername(userEntity.getUsername());
      userEntity.setEmail(userEntity.getEmail());

      UserEntity user = userRepo.save(userEntity);

      /// i will create cart By Default when the user is created
      Cart cart = new Cart();
      cart.setUser(userEntity);
      cartRepo.save(cart);
      // kafkaService.sendMessage("welcome_Email_Send", new UserEmailSendDto(user));
      return reqRes.sendSuccessResponse(201, "userCreated successfully done!").sendResponseEntity();
  }

  public ResponseEntity<ReqRes<UserEntity>> login(LoginDto loginDto, HttpServletRequest httpRequest, HttpServletResponse response) {
    ReqRes<UserEntity> Reqrepsonse = new ReqRes<>();

      // 1. Check if the user exists
      UserEntity user = userRepo.findByEmail(loginDto.getEmail());
      if (user == null) {
        // User not found
        Reqrepsonse.setMessage("User not found with this email");
        Reqrepsonse.setStatusCode(404);
      } else {
        // 2. Validate the password
        Boolean isPasswordValid = passwordEncoder.matches(loginDto.getPassword(), user.getPassword());
        if (isPasswordValid.equals(false)) {
          // Password not valid
          Reqrepsonse.setMessage("Password not valid");
          Reqrepsonse.setStatusCode(400);
        } else {
          // 3. Generate the token
          String token = jwtUtil.generateToken(user);
          String refreshToken = jwtUtil.generateRefreshToken(user);

          // 4. Set the cookie
          Cookie accessToken = new Cookie("accessToken", token);
          Cookie setRefreshToken = new Cookie("refreshToken", refreshToken);

          // 5. Set the token and refreshToken in the cookies
          accessToken.setMaxAge(1 * 60);
          accessToken.setPath("/");
          accessToken.setPath("/");
          // accessToken.setHttpOnly(true);
          // accessToken.setSecure(true);
          setRefreshToken.setPath("/");
          setRefreshToken.setMaxAge(24 * 60 * 60 * 60);

          // 6. Add the cookies to the response
          response.addCookie(accessToken);
          response.addCookie(setRefreshToken);

          // 7. Set the refreshToken in the user entity
          user.setRefreshToken(refreshToken);

          // 8. Return the response
          Reqrepsonse.sendSuccessResponse(200, "Login successfully done!");

          // 9. Save the user entity
          userRepo.save(user);
        }
      }
    return Reqrepsonse.sendResponseEntity();
  }

  public ResponseEntity<ReqRes<String>> getnewAccessToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
    ReqRes<String> response = new ReqRes<String>();
    Cookie[] cookies = httpServletRequest.getCookies();
    String refreshToken = "";
    if (cookies.length == 0) {
      response.sendErrorMessage(401 , "Cookies not found please login ");
    }
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals("refreshToken")) {
        refreshToken = cookie.getValue();
        break;
      }
    }
    if (refreshToken == null || refreshToken.equals("")) {
      return response.sendErrorMessage(401 , "refresh token not found").sendResponseEntity();
    }
    String email = jwtUtil.extractEmail(refreshToken);
    UserEntity foundUser = userRepo.findByEmail(email);
    if (foundUser == null || foundUser.equals(null)) {
      // throw new Exception("User not found");
      response.sendErrorMessage(401 , "User not found with this token");
    }
    if (!foundUser.getRefreshToken().equals(refreshToken)) {
      response.setMessage("Please login again ");
      response.setStatusCode(500);
      // httpServletResponse.addCookie("refreshToken");
      Cookie expiredCookie = new Cookie("refreshToken", "");
      expiredCookie.setMaxAge(0);
      httpServletResponse.addCookie(expiredCookie);
      return response.sendResponseEntity();
    }
    String token = jwtUtil.generateToken(foundUser);
    Cookie accessToken = new Cookie("accessToken", token);
    httpServletResponse.addCookie(accessToken);
    return response.sendSuccessResponse(200, "new token generated", token).sendResponseEntity();
  }
}
