package com.oauth.jwtauth.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
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
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("accessToken")) {
              authHeader  = cookie.getValue();
            }
        }
        String username = jwtUtil.extractUsername(authHeader);
        UserEntity user = userRepo.findByUsername(username);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                username, null, user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
  }
  
}
