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
        return new OpenAPI().info(
          new Info()
               .title("Research Paper API")
               .version("1.0")
               .description("Auth APIs + Research Paper")
                )
                .addSecurityItem(
                      new SecurityRequirement().addList("bearerAuth")
                )
                .components(
                new Components().addSecuritySchemes(
                                        "bearerAuth",
                 new SecurityScheme().name("bearerAuth")
                         .type(SecurityScheme.Type.HTTP)
                         .scheme("bearer")
                         .bearerFormat("JWT")
                )
                ).addServersItem(
                        new Server()
                                .url("https://researchpaper.site")
                                .description("Production Server")
                )
                ;
    }
}