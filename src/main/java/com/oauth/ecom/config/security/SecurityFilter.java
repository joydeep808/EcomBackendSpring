package com.oauth.ecom.config.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityFilter {
  @Autowired
  private AuthenticationProvider authenticationProvider;
  @Autowired
  private JwtConfig jwtConfig;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    String defaultUrl = "/api/v1";
    String ProductMappingUrl = "/api/v1/product";
    String categoryMappingUrl = "/api/v1/category";
    String OrderMapping = "/api/v1/order";
    String AddressMapping = "/api/v1/address";
    String CartMapping = "/api/v1/cart/";
    String CouponCodeMapping = "/api/v1/couponCode/";
    httpSecurity
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider)
        .authorizeHttpRequests(authConfig -> {
          authConfig.requestMatchers(defaultUrl+"/" ,
          defaultUrl+"/user/login",
          defaultUrl+"/user/register"
          ).permitAll();
          // Admin Route
          //Product Routes
          authConfig.requestMatchers(HttpMethod.GET ,
          "/api/v1/user/private").hasAnyAuthority("ADMIN");
          authConfig.requestMatchers(
          ProductMappingUrl+"/update" ,
          defaultUrl+"/user/private" ,
          ProductMappingUrl+"/create" ,
          defaultUrl+"/user/avatar" ,
          defaultUrl+"/user/update",
          categoryMappingUrl+"/create",
          categoryMappingUrl+"/update",
          CouponCodeMapping+"/create"
          ).permitAll();
          authConfig.requestMatchers(HttpMethod.POST ,
          "/api/v1/c/create").permitAll();
          /// Category Routes

          // User And Admin Both have the access
          authConfig.requestMatchers(defaultUrl+"/user/avatar" ,
          defaultUrl+"/user/update",
          defaultUrl+"/user/info" ,
          defaultUrl+"/user/info",
          categoryMappingUrl+"/{name}",
          ProductMappingUrl+"/{id}",
          ProductMappingUrl+"/{id}",
          CartMapping+"add" ,
          CartMapping+"delete" ,
          CartMapping+"get",
          AddressMapping+"/create",
          AddressMapping+"/update",
          AddressMapping+"/get",
          OrderMapping+"/buy",
          CouponCodeMapping+"/add",
          CouponCodeMapping+"/remove"
          ).permitAll();

          // Any user can access this routes
          authConfig.requestMatchers(ProductMappingUrl+"/p/{name}" ,
          ProductMappingUrl+"/{id}",
          ProductMappingUrl+"/p/r/all",
          ProductMappingUrl+"/p/{name}" ,
          ProductMappingUrl+"/p/c/{category}",
          ProductMappingUrl+"/p/{page}/{maxProducts}",
          OrderMapping+"/request",
          OrderMapping+"/callback"

          ).permitAll();
        }).exceptionHandling(e -> {
          e.accessDeniedHandler(DeniedHandler());
        });
    return httpSecurity.build();
  }

  @Bean
  public AccessDeniedHandler DeniedHandler() {
    return (req, res, accesDeniedException) -> {
      res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied" + accesDeniedException.getMessage());
    };
  }

  @Bean
  CorsConfigurationSource configurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(false);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
