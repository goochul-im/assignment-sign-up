package com.thinkfree.tfinder.common.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.SocketOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(host, port);
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(5)) // 명령 실행 타임아웃
                .clientOptions(ClientOptions.builder()
                        .autoReconnect(true) // 자동 재연결
                        .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS) // disconnect 시 커맨드 동작
                        .build()
                ).build();

        return new LettuceConnectionFactory(redisConfig, clientConfig);
    }

//    @Bean("stringRedisTemplate")
//    public StringRedisTemplate redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
//        return new StringRedisTemplate(redisConnectionFactory);
//    }

}
