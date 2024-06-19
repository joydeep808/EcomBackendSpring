package com.oauth.ecom.services.email.emailtemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
  @Value("${spring.mail.username}")
  private String email;
  @Autowired JavaMailSender javaMailSender;
  EmailTemplates emailTemplates = new EmailTemplates();
  
  public boolean welcomEmailSend(String to , String name){
    if ( name == null) {
      name += "User";
    }
    try {
    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);
    
      helper.setTo(to);
      helper.setText(emailTemplates.emailOnboardingSend(name), true);
      helper.setFrom(new InternetAddress(email , "Joydeep Debanth"));
      helper.setSubject("OnBoarding Conformation");
      javaMailSender.send(message);
      return true;
  } catch (Exception e) {
    System.err.println(e.getLocalizedMessage());
    return false;
    // TODO: handle exception
  }
  }
}
