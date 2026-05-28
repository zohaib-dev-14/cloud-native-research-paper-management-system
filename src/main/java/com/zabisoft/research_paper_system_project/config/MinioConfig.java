package com.zabisoft.research_paper_system_project.config;



import io.minio.Http;
import io.minio.MinioClient;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.HttpURLConnection;


//@Configuration
public class MinioConfig {

    @Value("${MINIO_URL}")
    private String url;
    @Value("${MINIO_ROOT_USER}")
    private String username;
    @Value("${MINIO_ROOT_PASSWORD}")
    private String password;
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(username, password)
                .build();
    }
}
