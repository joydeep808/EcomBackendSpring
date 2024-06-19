package com.oauth.jwtauth.config.security;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.oauth.jwtauth.entity.UserEntity;
import com.oauth.jwtauth.repository.UserRepo;
import com.oauth.jwtauth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;






@Component
public class JwtConfig extends OncePerRequestFilter{
  @Autowired
  private JwtUtil jwtUtil;
  @Autowired
  private UserRepo userRepo;
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
          filterChain.doFilter(request, response);
          return ;
        }
        String authHeader = "";
        String refreshToken = "";
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("accessToken")) {
              authHeader  = cookie.getValue();
            }
            if (cookie.getName().equals("refreshToken")) {
              refreshToken = cookie.getValue();
            }
        }
        if (authHeader.equals("") && refreshToken.equals("")) {
          filterChain.doFilter(request, response);
          return;
        }
        if (authHeader == null || authHeader.equals("")) {
            String email = jwtUtil.extractEmail(refreshToken);
            if (email == null ) {
              Cookie deleteRefershCookie =  new Cookie("refreshToken", "");
             deleteRefershCookie.setMaxAge(0);
             response.addCookie(deleteRefershCookie);
              filterChain.doFilter(request, response);
              return;
            }
            UserEntity user = userRepo.findByEmail(email);
            if (user  == null) {
              Cookie deleteRefershCookie =  new Cookie("refreshToken", "");
             deleteRefershCookie.setMaxAge(0);
             response.addCookie(deleteRefershCookie);
              filterChain.doFilter(request, response);
              return;
            }
            if (!refreshToken.equals(user.getRefreshToken())) {
             Cookie deleteRefershCookie =  new Cookie("refreshToken", "");
             deleteRefershCookie.setMaxAge(0);
             response.addCookie(deleteRefershCookie);
              filterChain.doFilter(request, response);
              return;
            }
            Boolean isExpired = jwtUtil.isTokenExpired(refreshToken);
            if (isExpired) {
              Cookie deleteRefershCookie =  new Cookie("refreshToken", "");
             deleteRefershCookie.setMaxAge(0);
             response.addCookie(deleteRefershCookie);
             filterChain.doFilter(request, response);
             return;
            }
            String token = jwtUtil.generateToken(user);
            Cookie accessToken = new Cookie("accessToken", token);
            accessToken.setMaxAge( 5* 60);
            response.addCookie(accessToken);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user.getUsername(), null , user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
          }
        else{
        String username = jwtUtil.extractUsername(authHeader);
        Collection<? extends GrantedAuthority> authorities = jwtUtil.extractAuthorities(authHeader);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        username, null,authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
        }
  }
  
}
