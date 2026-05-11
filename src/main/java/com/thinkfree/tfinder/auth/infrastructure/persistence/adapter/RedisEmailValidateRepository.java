package com.thinkfree.tfinder.auth.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IEmailValidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisEmailValidateRepository implements IEmailValidateRepository {

    private final String KEY_PREFIX = "email:validation:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(String email, Duration expiration) {
        redisTemplate.opsForValue().set(getKey(email), "", expiration);
    }

    @Override
    public boolean isValidate(String email) {
        return redisTemplate.hasKey(getKey(email)); // 어차피 트랜잭션이랑 pipeline에 묶일 일 없음
    }

    @Override
    public boolean delete(String email) {
        return redisTemplate.delete(getKey(email));
    }

    private String getKey(String email) {
        return KEY_PREFIX + email;
    }
}
