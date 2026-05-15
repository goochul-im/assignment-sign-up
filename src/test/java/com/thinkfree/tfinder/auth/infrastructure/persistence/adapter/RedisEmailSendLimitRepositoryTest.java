package com.thinkfree.tfinder.auth.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.annotation.IntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@IntegrationTest
class RedisEmailSendLimitRepositoryTest {

    private RedisEmailSendLimitRepository repository;

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    private LettuceConnectionFactory connectionFactory;

    @AfterEach
    void cleanUp() {
        if (connectionFactory != null) {
            connectionFactory.destroy();
        }
    }

    private StringRedisTemplate redisTemplate() {
        connectionFactory = new LettuceConnectionFactory(redis.getHost(), redis.getMappedPort(6379));
        connectionFactory.afterPropertiesSet();

        StringRedisTemplate redisTemplate = new StringRedisTemplate(connectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Test
    void 잔여_메일_발송량을_가져올_수_있다(){
        //given
        long id = 1;
        String remainLimit = "10";
        repository = new RedisEmailSendLimitRepository(redisTemplate());
        redisTemplate().opsForValue().set("send:limit" + id, remainLimit);

        //when
        int result = repository.getRemainLimit(50, id);

        //then
        assertThat(result).isEqualTo(Integer.valueOf(remainLimit));
    }

    @Test
    void 잔여_메일_발송량이_없을_경우_mailLimit를_저장하고_그대로_반환한다(){
        //given
        long id = 1;
        StringRedisTemplate template = redisTemplate();
        repository = new RedisEmailSendLimitRepository(template);

        //when
        int mailLimit = 30;
        int result = repository.getRemainLimit(mailLimit, id);
        String getLimit = template.opsForValue().get("send:limit:" + id);

        //then
        assertThat(result).isEqualTo(mailLimit);
        assertThat(getLimit).isNotNull();
        assertThat(Integer.valueOf(getLimit)).isEqualTo(mailLimit);
    }

}
