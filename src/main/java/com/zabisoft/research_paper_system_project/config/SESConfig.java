package com.zabisoft.research_paper_system_project.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;

@Configuration
public class SESConfig {

    @Value("${aws.region}")
    private String region;
    @Bean
   public SesV2Client sesV2Client() {
        return SesV2Client.builder().region(Region.of(region)).build();
    }
}
