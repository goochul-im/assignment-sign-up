package com.thinkfree.tfinder.common.config;

import io.lettuce.core.RedisClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;

@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnection connection(RedisClient redisClient) {
        LettuceConnection connection = new LettuceConnection(1000L, redisClient);

    }

    @Bean
    public RedisClient redisClient() {
    }

}
