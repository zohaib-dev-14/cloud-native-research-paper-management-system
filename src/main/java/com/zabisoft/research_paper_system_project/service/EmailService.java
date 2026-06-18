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
<!-- Header --> <tr> <td align="center" style=" background:#2563eb; padding:35px; color:white; "> <h1 style=" margin:0; font-size:28px; font-weight:bold; "> Research Paper System </h1> <p style=" margin-top:10px; font-size:15px; opacity:0.9; "> Secure Account Verification </p> </td> </tr> <!-- Content --> <tr> <td style="padding:40px;"> <h2 style=" color:#1e293b; margin-top:0; "> Verify Your Identity </h2> <p style=" color:#475569; font-size:15px; line-height:1.7; "> Hello, </p> <p style=" color:#475569; font-size:15px; line-height:1.7; "> We received a request to verify your account. Please use the One-Time Password (OTP) below to continue. </p> <!-- OTP BOX --> <div style=" text-align:center; margin:35px 0; "> <div style=" display:inline-block; background:#eff6ff; border:2px dashed #2563eb; border-radius:12px; padding:20px 35px; font-size:36px; font-weight:bold; letter-spacing:8px; color:#2563eb; "> %s </div> </div> <p style=" color:#475569; font-size:15px; text-align:center; "> This OTP will expire in <strong style="color:#dc2626;">5 minutes</strong> </p> <!-- Security Box --> <div style=" background:#fff7ed; border-left:5px solid #f97316; border-radius:8px; padding:18px; margin-top:30px; "> <h3 style=" margin-top:0; color:#c2410c; font-size:16px; "> Security Reminder </h3> <ul style=" color:#7c2d12; padding-left:20px; margin-bottom:0; "> <li>Never share this OTP with anyone.</li> <li>Our team will never ask for your OTP.</li> <li>If you did not request this verification, ignore this email.</li> </ul> </div> <p style=" margin-top:35px; color:#475569; font-size:15px; line-height:1.7; "> Thank you for using <strong>Research Paper System</strong>. </p> <p style=" color:#475569; font-size:15px; "> Regards,<br> <strong>Research Paper System Team</strong> </p> </td> </tr> <!-- Footer --> <tr> <td align="center" style=" background:#f8fafc; border-top:1px solid #e2e8f0; padding:25px; "> <p style=" margin:0; color:#64748b; font-size:13px; "> © 2026 Research Paper System </p> <p style=" margin-top:8px; color:#94a3b8; font-size:12px; "> Secure • Reliable • Scalable </p> </td> </tr>
""".formatted(otp);

        Content content = new Content("text/html", htmlContent);
        return content;
    }
}
