// package com.oauth.jwtauth.services.email;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.mail.javamail.MimeMessageHelper;
// import org.springframework.stereotype.Service;

// import com.oauth.jwtauth.services.email.emailtemplate.templates;

// import jakarta.mail.internet.MimeMessage;

// @Service
// public class EmailService {
//   @Autowired(required = true)
//   private JavaMailSender javaMailSender;
//   MimeMessage message = javaMailSender.createMimeMessage();
//  MimeMessageHelper helper = new MimeMessageHelper(message);
//   templates templates = new templates();
// public boolean sendOnBoardingMail(String email ,String name){
//     try {
//       helper.setTo(email);
//       helper.setText(  templates.emailOnboardingSend(name),true);
//       helper.setSubject("Welcome Mail send");
//       javaMailSender.send(message);
//       return true;
//     } catch (Exception e) {
//       return false;
//     }
// }
// public boolean sendResetPasswordMail(String email , String link , String name){
//   return true;
// }
// }



//    // message scheduling 
//   // int email  = 0;
//   // @Scheduled(cron ="0 */2 * ? * *" )
//   // public void sendEmailToTheUser() throws MessagingException{
//   //   List<UserEntity> users = userRepo.findAll();
    
//   //   for (UserEntity userEntity : users) {
//   //     MimeMessage message = javaMailSender.createMimeMessage();
//   //     MimeMessageHelper helper = new MimeMessageHelper(message , true);
//   //     helper.setTo(userEntity.getEmail());
//   //     helper.setSubject("Schuduling");
//   //     helper.setText("df", true);
//   // javaMailSender.send(message);
//   //   }
//   //   email++;
//   // }
