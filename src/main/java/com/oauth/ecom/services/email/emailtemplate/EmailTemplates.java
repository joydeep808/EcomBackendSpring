package com.oauth.ecom.services.email.emailtemplate;



public class EmailTemplates {
  String websiteLink = "youtube.com";
  String websiteName = "Joydeep Debnath";
  public String emailOnboardingSend(String name ){
    String template = """
        <h1 style="text-align:center; color:black;">Hello ${name}</h1>
        
  <h3 style=color:#525f7f>Welcome to our website ${name} ! We're very excited to have you on board</h3>
  <hr style="width:100%;border:none;border-top:1px solid #eaeaea;border-color:#e6ebf1;margin:20px 0" />
  <p style="font-size:16px;line-height:24px;margin:16px 0;color:#525f7f;text-align:left">Thanks for submitting your account information. You&#x27;re now ready to manage your expense smoothly!</p>

  <p style="font-size:16px;line-height:24px;margin:16px 0;color:#525f7f;text-align:left">You can access Your Account from The Website Fully</p>

  <a href="${websiteLink}" style="background-color:#656ee8;border-radius:5px;color:#fff;font-size:16px;font-weight:bold;text-decoration:none;text-align:center;display:inline-block;width:100%;padding:10px 10px 10px 10px;line-height:100%;max-width:100%" target="_blank"><span><!--[if mso]><i style="letter-spacing: 10px;mso-font-width:-100%;mso-text-raise:15" hidden>&nbsp;</i><![endif]--></span><span style="max-width:95%;display:inline-block;line-height:120%;mso-padding-alt:0px;mso-text-raise:7.5px">View Your Account</span><span><!--[if mso]><i style="letter-spacing: 10px;mso-font-width:-100%" hidden>&nbsp;</i><![endif]--></span></a>
<hr style="width:100%;border:none;border-top:1px solid #eaeaea;border-color:#e6ebf1;margin:20px 0" />

  <p style="font-size:16px;line-height:24px;margin:16px 0;color:#525f7f;text-align:left">Once you&#x27;re verify email, you can start  <!-- --></p>
  <p style="font-size:16px;line-height:24px;margin:16px 0;color:#525f7f;text-align:left">— Joydeep Debnath </p>
  <hr style="width:100%;border:none;border-top:1px solid #eaeaea;border-color:#e6ebf1;margin:20px 0" />
  <p style="font-size:12px;line-height:16px;margin:16px 0;color:#8898aa">Joydeep Debnath Agartala Tripura West </p>
  <h3>Need help, or have questions? Just reply to this email, we'd love to help.</h3>
  <p>Thank You</p>
  """.replace("${name}", name).replace("${websiteLink}", websiteLink);
    return template; 
  }
}
