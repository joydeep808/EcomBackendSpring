package com.oauth.ecom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Configuration
public class RazorPayConfiguration {
   @Value("${spring.razorpay.key}")
 private String razorpayKey ;
 @Value("${spring.razorpay.secret}")
  private String razorPaySecret;
  @Bean
  public RazorpayClient razorpayClient() throws RazorpayException{
    return new RazorpayClient(razorpayKey , razorPaySecret);
  }


}
