package com.zabisoft.research_paper_system_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {
    @Bean
    StringRedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}
