package com.zabisoft.research_paper_system_project.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()

                .info(new Info()
                        .title("Cloud-Native Research Paper Management System API")
                        .version("1.0")
                        .description("Production-ready backend system using Spring Boot, JWT, Redis, Docker, AWS and CI/CD.")
                )

                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))

                .components(new Components()
                        .addSecuritySchemes(
                                "Bearer Authentication",

                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}