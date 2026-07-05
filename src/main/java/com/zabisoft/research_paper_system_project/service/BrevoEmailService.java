package com.zabisoft.research_paper_system_project.service;

import com.zabisoft.research_paper_system_project.interfaces.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrevoEmailService implements EmailService {

    private final JavaMailSender mailSender; // Brevo SMTP settings auto-injected

    private void sendHtmlEmail(String toEmail, String subject, String htmlContent) {
        try {
    // multipurpose internet mail extension (MIME) -> used for different services, brevo, MailGun and more
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("no-reply@researchpaper.site", "ScholarFlow");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true means HTML content
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Brevo se email bhejny mein error: " + e.getMessage());
        }
    }


    /**
     * @param toEmail
     * @param otp
     */
    @Override
    public void sendOtp(String toEmail, String otp) {
        String subject =
                "ScholarFlow - OTP Verification";

        String html =
                getOtpHtml(otp);

        sendHtmlEmail(toEmail, subject, html);
    }

    /**
     * @param toEmail
     * @param username
     */
    @Override
    public void sendResetConfirmation(String toEmail, String username) {

        String subject = "ScholarFlow - OTP Verification";

        @NonNull String resetContent = getResetContent(username);

        sendHtmlEmail(toEmail, subject, resetContent);
    }


    private static @NonNull String getResetContent(String username) {
        return String.format("""
<!DOCTYPE html>
<html>
<body style="margin:0;padding:0;background:#f1f5f9;font-family:Arial,sans-serif;">
<table width="100%%" cellpadding="0" cellspacing="0">
  <tr>
    <td align="center" style="padding:40px 20px;">
      <table width="600" cellpadding="0" cellspacing="0"
             style="background:white;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);">

        <!-- Header -->
        <tr>
          <td align="center" style="background:#2563eb;padding:35px;color:white;">
            <h1 style="margin:0;font-size:28px;font-weight:bold;">
           ScholarFlow - Towards Future
            </h1>
            <p style="margin-top:10px;font-size:15px;opacity:0.9;">
              Account Security Notification
            </p>
          </td>
        </tr>

        <!-- Content -->
        <tr>
          <td style="padding:40px;">
            <h2 style="color:#1e293b;margin-top:0;">
              Password Reset Successful
            </h2>

            <p style="color:#475569;font-size:15px;line-height:1.7;">
              Hello, <strong>%s</strong>
            </p>

            <p style="color:#475569;font-size:15px;line-height:1.7;">
              Your password for <strong>ScholarFlow</strong> has been
              successfully reset. You can now log in with your new password.
            </p>

            <!-- Success Box -->
            <div style="text-align:center;margin:35px 0;">
              <div style="display:inline-block;background:#f0fdf4;
                          border:2px solid #16a34a;border-radius:12px;
                          padding:20px 35px;">

                <p style="margin:0;font-size:18px;font-weight:bold;color:#16a34a;">
                  ✓ &nbsp; Password Updated Successfully
                </p>

                <p style="margin:8px 0 0;font-size:13px;color:#15803d;">
                  Your account is secure and ready to use.
                </p>
              </div>
            </div>

            <!-- Warning Box -->
            <div style="background:#fff7ed;border-left:5px solid #f97316;
                        border-radius:8px;padding:18px;margin-top:30px;">

              <h3 style="margin-top:0;color:#c2410c;font-size:16px;">
                Wasn't You?
              </h3>

              <ul style="color:#7c2d12;padding-left:20px;
                         margin-bottom:0;font-size:14px;line-height:1.8;">

                <li>If you did not request this password reset, your account may be compromised.</li>
                <li>Contact our support team immediately.</li>
                <li>Consider changing your password again from a secure device.</li>
              </ul>
            </div>

            <p style="margin-top:35px;color:#475569;font-size:15px;line-height:1.7;">
              Thank you for using <strong>ScholarFlow - Towards Future</strong>.
            </p>

            <p style="color:#475569;font-size:15px;">
              Regards,<br>
              <strong>ScholarFlow Team</strong>
            </p>
          </td>
        </tr>

        <!-- Footer -->
        <tr>
          <td align="center"
              style="background:#f8fafc;border-top:1px solid #e2e8f0;padding:25px;">

            <p style="margin:0;color:#64748b;font-size:13px;">
              © 2026 ScholarFlow - Towards Future
            </p>

            <p style="margin-top:8px;color:#94a3b8;font-size:12px;">
              Secure • Reliable • Scalable
            </p>
          </td>
        </tr>

      </table>
    </td>
  </tr>
</table>
</body>
</html>
""", username);


    }
    private String getOtpHtml(String otp) {

        return """
      <!-- Header --> <tr> <td align="center" style=" background:#2563eb; padding:35px; color:white; "> <h1 style=" margin:0; font-size:28px; font-weight:bold; "> ScholarFlow - Towards Future </h1> <p style=" margin-top:10px; font-size:15px; opacity:0.9; "> Secure Account Verification </p> </td> </tr> <!-- Content --> <tr> <td style="padding:40px;"> <h2 style=" color:#1e293b; margin-top:0; "> Verify Your Identity </h2> <p style=" color:#475569; font-size:15px; line-height:1.7; "> Hello, </p> <p style=" color:#475569; font-size:15px; line-height:1.7; "> We received a request to verify your account. Please use the One-Time Password (OTP) below to continue. </p> <!-- OTP BOX --> <div style=" text-align:center; margin:35px 0; "> <div style=" display:inline-block; background:#eff6ff; border:2px dashed #2563eb; border-radius:12px; padding:20px 35px; font-size:36px; font-weight:bold; letter-spacing:8px; color:#2563eb; "> %s </div> </div> <p style=" color:#475569; font-size:15px; text-align:center; "> This OTP will expire in <strong style="color:#dc2626;">5 minutes</strong> </p> <!-- Security Box --> <div style=" background:#fff7ed; border-left:5px solid #f97316; border-radius:8px; padding:18px; margin-top:30px; "> <h3 style=" margin-top:0; color:#c2410c; font-size:16px; "> Security Reminder </h3> <ul style=" color:#7c2d12; padding-left:20px; margin-bottom:0; "> <li>Never share this OTP with anyone.</li> <li>Our team will never ask for your OTP.</li> <li>If you did not request this verification, ignore this email.</li> </ul> </div> <p style=" margin-top:35px; color:#475569; font-size:15px; line-height:1.7; "> Thank you for using <strong>ScholarFlow - Towards Future</strong>. </p> <p style=" color:#475569; font-size:15px; "> Regards,<br> <strong>ScholarFlow Team</strong> </p> </td> </tr> <!-- Footer --> <tr> <td align="center" style=" background:#f8fafc; border-top:1px solid #e2e8f0; padding:25px; "> <p style=" margin:0; color:#64748b; font-size:13px; "> © 2026 ScholarFlow - Towards Future </p> <p style=" margin-top:8px; color:#94a3b8; font-size:12px; "> Secure • Reliable • Scalable </p> </td> </tr>
    """.formatted(otp);

    }
}
