package com.zabisoft.research_paper_system_project.service;


import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {
    @Value("${spring.sendgrid.api-key}")
     private String sendGridApiKey;

    public void sendOtp(String toEmail, String otp) {
        Email from = new Email("no-reply@researchpaper.site", "Research Paper System");
        Email to = new Email(toEmail);

        String subject = "Research Paper System - OTP Verification";

        Content content = getContent(otp);

        Mail mail = new Mail(from, subject, to, content);
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

    private static @NonNull Content getContent(String otp) {
        String htmlContent = """
<html>
<body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">

    <h2 style="color: #2c3e50;">
        <b>Research Paper System</b>
    </h2>

    <p>Hello,</p>

    <p>Your One-Time Password (OTP) for verification is:</p>

    <div style="
        font-size: 32px;
        font-weight: bold;
        color: #2563eb;
        letter-spacing: 5px;
        margin: 20px 0;
    ">
        %s
    </div>

    <p>
        This OTP will expire in <b>5 minutes</b>.
    </p>

    <p>
        For your security:
    </p>

    <ul>
        <li>Do not share this OTP with anyone</li>
        <li>Our team will never ask for your OTP</li>
        <li>If you did not request this code, please ignore this email and report</li>
    </ul>

    <br>

    <p>
        Regards,<br>
        <b>Research Paper System Team</b>
    </p>

</body>
</html>
""".formatted(otp);

        Content content = new Content("text/html", htmlContent);
        return content;
    }
}
