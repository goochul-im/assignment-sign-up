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
        redisTemplate.opsForSet().add(key, workspaceUrl); //set이라서 중복 저장이 안됨
        redisTemplate.expire(key, expiration);
//         이 방식은.. 초대별 TTL이 안된다.
        // 초대별로 바꾸려면 어떻게 해야하지..?
    }

    @Override
    public Set<String> findWorkspaceUrlsByEmail(String email) {
        Set<String> workspaceUrls = redisTemplate.opsForSet().members(getKey(email));
        if (workspaceUrls == null) return Set.of();

        return workspaceUrls;
    }

    @Override
    public boolean delete(String email) {
        return redisTemplate.delete(getKey(email));
    }

    private String getKey(String email) {
        return KEY_PREFIX + email;
    }
}
