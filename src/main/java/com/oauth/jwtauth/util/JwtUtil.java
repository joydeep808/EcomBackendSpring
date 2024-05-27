package com.oauth.jwtauth.util;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.oauth.jwtauth.entity.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component

public class JwtUtil {
  private SecretKey key;
  private static final long expirationTime = 864000;

  public JwtUtil(){
    String secretKey = "fdksjlfjslkdfjsjfsjflkhjriowuroiwe";
    byte[] keyBytes  = Base64.getEncoder().encode(secretKey.getBytes());
    this.key = new SecretKeySpec(keyBytes , "HmacSHA256");

  }
  public String generateToken(UserEntity user ){
    String token  = Jwts.builder().subject(user.getUsername()).claim("email", user.getEmail()).claim("role", user.getRole()).issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis() + 10 * 60 * 60 * 1000)).signWith(key).compact();
    return token;
  }
  public String generateRefreshToken(HashMap<String , Object> claims , UserEntity user){
    return Jwts.builder().claims(claims).issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis() + expirationTime)).signWith(key).compact();
  }


  public String extractUsername(String token){
    return extractClaims(token , Claims::getSubject);
  }
  private <T> T  extractClaims(String token, Function<Claims , T> claimFunction) {
    return claimFunction.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());
  }
  public boolean isTokenValid (String token , UserDetails userEntity){
    final String username = extractUsername(token);
    return (username.equals(userEntity.getUsername()) && !isTokenExpired(token));
  }
  private boolean isTokenExpired(String token) {
    return extractClaims(token , Claims::getExpiration).before(new Date());
  }
}
