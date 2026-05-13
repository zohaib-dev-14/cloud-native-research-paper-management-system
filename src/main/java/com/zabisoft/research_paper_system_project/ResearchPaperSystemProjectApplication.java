package com.zabisoft.research_paper_system_project;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ResearchPaperSystemProjectApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        System.setProperty(
                "spring.sendgrid.api-key",
                dotenv.get("SENDGRID_API_KEY")
        );

        System.setProperty(
                "spring.sendgrid.proxy.host",
                dotenv.get("SENDGRID_PROXY_HOST")
        );
        System.setProperty(
                "JWT_SECRET",
                dotenv.get("SECRET_KEY")
        );

        System.setProperty(
                "server.ssl.key-store-password",
                dotenv.get("SSL_KEY_STORE_PASSWORD")
        );
        SpringApplication.run(ResearchPaperSystemProjectApplication.class, args);
    }

}
