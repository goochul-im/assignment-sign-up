package com.thinkfree.tfinder.auth.infrastructure.persistence.adapter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class RedisRefreshTokenRepositoryTest {

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    private LettuceConnectionFactory connectionFactory;

    @AfterEach
    void tearDown() {
        if (connectionFactory != null) {
            connectionFactory.destroy();
        }
    }

    @Test
    void 리프레시_토큰을_저장하고_ttl과_함께_조회할_수_있어야_한다() {
        //given
        String email = "test@email.com";
        String refreshToken = "refreshToken";
        StringRedisTemplate redisTemplate = redisTemplate();
        RedisRefreshTokenRepository repository = new RedisRefreshTokenRepository(redisTemplate);

        //when
        repository.save(email, refreshToken, Duration.ofSeconds(60));

        //then
        Optional<String> savedRefreshToken = repository.findByEmail(email);
        Long expireSeconds = redisTemplate.getExpire("refresh:" + email);
        assertThat(savedRefreshToken).contains(refreshToken);
        assertThat(expireSeconds).isPositive();
    }

    @Test
    void 리프레시_토큰을_삭제할_수_있어야_한다() {
        //given
        String email = "test@email.com";
        StringRedisTemplate redisTemplate = redisTemplate();
        RedisRefreshTokenRepository repository = new RedisRefreshTokenRepository(redisTemplate);
        repository.save(email, "refreshToken", Duration.ofSeconds(60));

        //when
        repository.deleteByEmail(email);

        //then
        assertThat(repository.findByEmail(email)).isEmpty();
    }

    private StringRedisTemplate redisTemplate() {
        connectionFactory = new LettuceConnectionFactory(redis.getHost(), redis.getMappedPort(6379));
        connectionFactory.afterPropertiesSet();

        StringRedisTemplate redisTemplate = new StringRedisTemplate(connectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
