package com.oauth.ecom.util;

import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.oauth.ecom.entity.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component

public class JwtUtil {
  private SecretKey key;
  private static final long expirationTime =600000 * 6 * 24 * 60;

  public JwtUtil(){
    String secretKey = "fdksjlfjslkdfjsjfsjflkhjriowuroiwe";
    byte[] keyBytes  = Base64.getEncoder().encode(secretKey.getBytes());
    this.key = new SecretKeySpec(keyBytes , "HmacSHA256");

  }
  public String generateToken(UserEntity user ){
    String token  = Jwts.builder().subject(user.getUsername()).claim("email", user.getEmail()).claim("id", user.getId()).claim("role", user.getRole()).claim("authorities" , user.getAuthorities()).issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis() + 30 * 60 * 60 * 1000 )).signWith(key).compact();
    return token;
  }
  public String generateRefreshToken(UserEntity user){
    return Jwts.builder().claim("email" , user.getEmail()).claim("id", user.getId()).issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis() + expirationTime)).signWith(key).compact();
  }

  public String extractUsername(String token){
    return extractClaims(token , Claims::getSubject);
  }

  public String extractEmail(String token){
    return extractClaims(token , claims->claims.get("email" , String.class));
  }
  
  public int extractId(String token){
    return extractClaims(token , claims->claims.get("id" , Integer.class));
  }
  public Collection<? extends GrantedAuthority> extractAuthorities(String token){
    return List.of(new SimpleGrantedAuthority(extractClaims(token , claims->claims.get("role" , String.class))));
  }
  
  
  private <T> T  extractClaims(String token, Function<Claims , T> claimFunction) {
    return claimFunction.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());
  }
  public boolean isTokenValid (String token , UserDetails userEntity){
    final String username = extractUsername(token);
    return (username.equals(userEntity.getUsername()) && !isTokenExpired(token));
  }
  public boolean isTokenExpired(String token) {
    return extractClaims(token , Claims::getExpiration).before(new Date());
  }
}
