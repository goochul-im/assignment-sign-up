package com.thinkfree.tfinder.auth.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IEmailValidateRepository;
import com.thinkfree.tfinder.auth.service.enumration.EmailValidateStatus;
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
    public void saveAsPending(String email, Duration expiration) {
        redisTemplate.opsForValue().set(getKey(email), EmailValidateStatus.PENDING.getStatus(), expiration);
    }

    @Override
    public void saveAsValidated(String email, Duration expiration) {
        redisTemplate.opsForValue().set(getKey(email), EmailValidateStatus.VALIDATE.getStatus(), expiration);
    }

    @Override
    public boolean isRequested(String email) {
        String byEmail = getByEmail(email);
        return byEmail != null && byEmail.equals(EmailValidateStatus.PENDING.getStatus());
    }

    @Override
    public boolean isValidated(String email) {
        String byEmail = getByEmail(email);
        return byEmail != null && byEmail.equals(EmailValidateStatus.VALIDATE.getStatus());
    }

    @Override
    public boolean delete(String email) {
        return redisTemplate.delete(getKey(email));
    }

    private String getKey(String email) {
        return KEY_PREFIX + email;
    }

    private String getByEmail(String email) {
        return redisTemplate.opsForValue().get(getKey(email));
    }
}
