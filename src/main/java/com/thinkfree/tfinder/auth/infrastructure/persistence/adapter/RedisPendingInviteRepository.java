package com.thinkfree.tfinder.auth.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IPendingInviteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisPendingInviteRepository implements IPendingInviteRepository {

    private static final String KEY_PREFIX = "invite:pending:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(String email, String workspaceUrl, Duration expiration) {
        String key = getKey(email);
        redisTemplate.opsForSet().add(key, workspaceUrl);
        redisTemplate.expire(key, expiration);
    }

    @Override
    public Set<String> findWorkspaceUrlsByEmail(String email) {
        Set<String> workspaceUrls = redisTemplate.opsForSet().members(getKey(email));
        if (workspaceUrls == null) return Set.of();

        return workspaceUrls;
    }

    @Override
    public void delete(String email) {
        redisTemplate.delete(getKey(email));
    }

    private String getKey(String email) {
        return KEY_PREFIX + email;
    }
}
