package com.thinkfree.tfinder.auth.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class RedisRefreshTokenRepository implements IRefreshTokenRepository {

    private final String KEY_PREFIX = "refresh:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean save(String email, String refreshToken, Duration expiration) {
        try {
            redisTemplate.opsForValue().set(getKey(email), refreshToken, expiration);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 인자로 인해 레디스에서 토큰이 저장되지 않았습니다. email = {}, refreshToken = {}, expiration = {}", email, refreshToken, expiration);
            return false;
        }
        return true;
    }

    @Override
    public Optional<String> findByEmail(String email) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(getKey(email)));
    }

    @Override
    public boolean deleteByEmail(String email)  {
        return redisTemplate.delete(getKey(email));
    }

    private String getKey(String email) {
        return KEY_PREFIX + email;
    }
}
