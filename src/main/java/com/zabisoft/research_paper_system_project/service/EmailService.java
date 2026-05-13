package com.zabisoft.research_paper_system_project.service;


import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {
    @Value("${spring.sendgrid.api-key}")
     private String sendGridApiKey;

    public void sendOtp(String toEmail, String otp) {
        Email from = new Email("no-reply@zabisoft.site", "ZabiSoft");
        Email to = new Email(toEmail);
        String text = "Your OTP code is: " + otp;
        Content content = new Content("text/plain", "Your OTP is: " + otp + "\nDo not Share this with anyone");

        Mail mail = new Mail(from, text, to, content);
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
