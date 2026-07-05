package com.zabisoft.research_paper_system_project.interfaces;

public interface EmailService {
    void sendOtp(String toEmail, String otp);
    void sendResetConfirmation(String toEmail, String username);
}
