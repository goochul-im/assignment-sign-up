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
        Boolean result = redisTemplate.hasKey(getKey(email));
        return result != null && result;
    }

    @Override
    public void delete(String email) {
        redisTemplate.delete(getKey(email));
    }

    private String getKey(String email) {
        return KEY_PREFIX + email;
    }
}
