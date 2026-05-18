package com.thinkfree.tfinder.auth.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.annotation.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@IntegrationTest
class RedisPendingInviteRepositoryTest {

    private RedisPendingInviteRepository repository;
    private LettuceConnectionFactory connectionFactory;

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @AfterEach
    void cleanUp() {
        if (connectionFactory != null) {
            connectionFactory.destroy();
        }
    }

    @Test
    void 초대_요청별로_만료시간을_적용할_수_있어야_한다() throws InterruptedException {
        // given
        String email = "test@email.com";
        String expiredWorkspaceUrl = "expired-workspace";
        String validWorkspaceUrl = "valid-workspace";
        repository = new RedisPendingInviteRepository(redisTemplate());

        repository.save(email, expiredWorkspaceUrl, Duration.ofMillis(200));
        repository.save(email, validWorkspaceUrl, Duration.ofSeconds(5));

        // when
        Thread.sleep(500);
        Set<String> workspaceUrls = repository.findWorkspaceUrlsByEmail(email);

        // then
        assertThat(workspaceUrls).containsExactly(validWorkspaceUrl);
    }

    private StringRedisTemplate redisTemplate() {
        connectionFactory = new LettuceConnectionFactory(redis.getHost(), redis.getMappedPort(6379));
        connectionFactory.afterPropertiesSet();

        StringRedisTemplate redisTemplate = new StringRedisTemplate(connectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
