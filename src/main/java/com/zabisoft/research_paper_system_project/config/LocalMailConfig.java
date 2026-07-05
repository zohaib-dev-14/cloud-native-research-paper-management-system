package com.zabisoft.research_paper_system_project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class LocalMailConfig {
    @Value("${SPRING_MAIL_HOST}")
    private String mailHost;

    @Value("${SPRING_MAIL_PORT}")
    private int mailPort;

    @Value("${SPRING_MAIL_USERNAME}")
    private String mailUsername;

    @Value("${SPRING_MAIL_PASSWORD}")
    private String mailPassword;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Setting host, port and credentials from environment variables
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);

        // Standard SMTP Security Protocols Config (Required for Brevo TLS)
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true"); // Force TLS encryption
        props.put("mail.debug", "true"); // Console logs mein email logs check karne ke liye (Optional)

        return mailSender;
    }
}
