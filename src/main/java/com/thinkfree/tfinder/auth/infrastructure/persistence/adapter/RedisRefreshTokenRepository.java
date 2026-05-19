package com.thinkfree.tfinder.auth.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IRefreshTokenRepository;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
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
        Boolean delete = redisTemplate.delete(getKey(email));
        if (delete == null){
            log.warn("파이프라인이나 트랜잭션 안에서 실행되었습니다.");
            throw new BusinessException(ErrorCode.EXTERNAL_ERROR);
        }
        return delete;
    }

    private String getKey(String email) {
        return KEY_PREFIX + email;
    }
}
