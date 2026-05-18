package com.thinkfree.tfinder.redis_test;

import com.thinkfree.tfinder.annotation.IntegrationTest;
import com.thinkfree.tfinder.auth.infrastructure.persistence.adapter.RedisRefreshTokenRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@IntegrationTest
public class RedisTest {

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);
    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    private LettuceConnectionFactory connectionFactory;

    @AfterEach
    void cleanUp() {
        if (connectionFactory != null) {
            connectionFactory.destroy();
        }
    }

    private StringRedisTemplate redisTemplate() {
        connectionFactory = new LettuceConnectionFactory();
        connectionFactory.afterPropertiesSet();

        StringRedisTemplate redisTemplate = new StringRedisTemplate(connectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Test
    void 레디스_테스트_1(){
        //given
        String email = "test@email.com";

        //when
        StringRedisTemplate template = redisTemplate();
        Boolean delete = template.delete(email); // 없는 키를 삭제하면 false
        Boolean noKey = template.expire("no key", Duration.ofSeconds(1000)); // 없는 키에 expire를 걸면 false
        template.opsForValue().set("my Key","my Value");
        String nullValue = template.opsForValue().get("no key");
        assertThrows(IllegalArgumentException.class, () -> template.opsForValue().set(null, email)); // null을 키로 주면 예외
        assertThrows(IllegalArgumentException.class, () -> template.opsForSet().add(null, email)); // null을 키로 주면 예외
        assertThrows(IllegalArgumentException.class, () -> template.expire("no key", null)); // Duration은 null로 주면 예외
        assertThrows(IllegalArgumentException.class, () -> template.delete((String) null)); // null을 삭제하면 예외

        template.opsForValue().set("dec", "10");
        Long decrement = template.opsForValue().decrement("dec", 15);
        String dec = template.opsForValue().get("dec");

        //then
        Assertions.assertThat(delete).isFalse();
        Assertions.assertThat(noKey).isFalse();
        Assertions.assertThat(nullValue).isNull();
        Assertions.assertThat(decrement).isEqualTo(-5);
        Assertions.assertThat(Integer.parseInt(dec)).isEqualTo(-5);
    }

    @Test
    void zset_테스트(){
        /**
         * 같은 key안의 value는 중복되어 저장되지 않는다.
         */
        //given
        StringRedisTemplate template = redisTemplate();
        String key = "myKey";
        String value = "myValue";
        template.opsForZSet().add(key, value, 100);
        template.opsForZSet().add(key, value, 200);

        //when
        Set<String> range = template.opsForZSet().range(key, 0, Long.MAX_VALUE);
        System.out.println(range);

        //then
        Assertions.assertThat(range.size()).isEqualTo(1);

        template.opsForZSet().add(key, "newValue", 300);
        range = template.opsForZSet().range(key, 0, Long.MAX_VALUE);
        Assertions.assertThat(range.size()).isEqualTo(2);
        Long remove = template.opsForZSet().remove(key, value);

        Assertions.assertThat(remove).isEqualTo(1);
        range = template.opsForZSet().range(key, 0, Long.MAX_VALUE);
        Assertions.assertThat(range.size()).isEqualTo(1);
    }

    @Test
    void 레디스_테스트_2(){
        //given
        String email = "test@email.com";

        //when & then
        redis.close();
        System.out.println("shutting down");
        assertThrows(IllegalStateException.class, () -> redisTemplate().delete(email));
        assertThrows(IllegalStateException.class, () -> redisTemplate().opsForValue().set(email, email));
        assertThrows(IllegalStateException.class, () -> redisTemplate().opsForSet().add(email, email));
        assertThrows(IllegalStateException.class, () -> redisTemplate().expire(email, Duration.ofSeconds(1000)));
        // redis와의 연결이 끊어졌을 경우에는 IllegalStateException이 던져짐
    }

    @Test
    void 레디스_테스트_3(){
        //given
        Long add = redisTemplate().opsForSet().add("new", "i1", "i2", "i3");
        Long add2 = redisTemplate().opsForSet().add("new", "a1", "a2");

        //when

        //then
        assertThat(add).isEqualTo(3L);
        assertThat(add2).isEqualTo(2L);
    }

}
