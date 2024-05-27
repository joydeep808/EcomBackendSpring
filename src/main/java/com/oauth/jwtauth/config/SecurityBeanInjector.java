package com.oauth.jwtauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.oauth.jwtauth.services.UserServiceDetails;


@Component
public class SecurityBeanInjector {
  @Autowired(required = true)
  private UserServiceDetails userServiceDetails;

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {

      return authenticationConfiguration.getAuthenticationManager(); //providerManager implements AuthenticationManager
  }

  @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userServiceDetails);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }
  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }
  
  // @Bean
  // public UserDetailsService userDetailsService(){
  //   return username -> {
  //       return userRepo.findByUsername(username);
  //       };
  //   }

  
}
