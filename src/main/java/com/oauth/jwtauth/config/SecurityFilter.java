package com.oauth.jwtauth.config;

import java.security.Permission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity

public class SecurityFilter {
  

  @Autowired
  private AuthenticationProvider authenticationProvider;
  @Autowired
  private JwtConfig jwtConfig;
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
    httpSecurity
    .csrf(csrf->csrf.disable()).sessionManagement(sess-> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authenticationProvider(authenticationProvider).addFilterBefore(jwtConfig, UsernamePasswordAuthenticationFilter.class).authorizeHttpRequests(authConfig -> {
                    authConfig.requestMatchers("/").permitAll();
                    authConfig.requestMatchers(HttpMethod.POST, "/register").permitAll();
                    authConfig.requestMatchers(HttpMethod.POST , "/login").permitAll();
                    authConfig.requestMatchers(HttpMethod.GET , "/private").hasAuthority("ADMIN");
                    authConfig.anyRequest().authenticated();
                });
                return httpSecurity.build();
  }
}
