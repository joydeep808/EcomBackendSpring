package com.oauth.jwtauth.services.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class EmailService {
  @Autowired(required = true)
  private JavaMailSender javaMailSender;
  
}
