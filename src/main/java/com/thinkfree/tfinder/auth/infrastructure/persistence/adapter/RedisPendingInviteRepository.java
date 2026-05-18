package com.thinkfree.tfinder.auth.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IPendingInviteRepository;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Repository
@Slf4j
@RequiredArgsConstructor
public class RedisPendingInviteRepository implements IPendingInviteRepository {

    private final String KEY_PREFIX = "invite:pending:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(String email, String workspaceUrl, Duration expiration) {
        String key = getKey(email);
        long now = Instant.now().toEpochMilli();
        long expiresAt = now + expiration.toMillis();

        removeExpiredInvites(key, now); // 이미 만료된 대기 요청들을 삭제
        if (redisTemplate.opsForZSet().add(key, workspaceUrl, expiresAt) == null) {
            log.warn("파이프라인이나 트랜잭션 안에서 실행되었습니다.");
            throw new BusinessException(ErrorCode.EXTERNAL_ERROR);
        }
        refreshKeyExpirationAtLatestInvite(key); // 가장 나중의 요청으로 zset 만료시간 설정
    }

    @Override
    public Set<String> findWorkspaceUrlsByEmail(String email) {
        String key = getKey(email);
        long now = Instant.now().toEpochMilli();

        removeExpiredInvites(key, now);
        Set<String> workspaceUrls = redisTemplate.opsForZSet().rangeByScore(key, now + 1, Double.POSITIVE_INFINITY);
        if (workspaceUrls == null) return Set.of();

        return workspaceUrls;
    }

    @Override
    public boolean delete(String email) {
        Boolean delete = redisTemplate.delete(getKey(email));
        if (delete == null) {
            log.warn("파이프라인이나 트랜잭션 안에서 실행되었습니다.");
            throw new BusinessException(ErrorCode.EXTERNAL_ERROR);
        }
        return delete;
    }

    @Override
    public boolean deleteOne(String email, String url) {
        Long remove = redisTemplate.opsForZSet().remove(getKey(email), url);
        if (remove == null) {
            log.warn("파이프라인이나 트랜잭션 안에서 실행되었습니다.");
            throw new BusinessException(ErrorCode.EXTERNAL_ERROR);
        }
        return remove > 0;
    }

    @Override
    public boolean isRemain(String email) {
        Set<String> range = redisTemplate.opsForZSet().range(getKey(email), 0, Long.MAX_VALUE);
        if (range == null) {
            log.warn("파이프라인이나 트랜잭션 안에서 실행되었습니다.");
            throw new BusinessException(ErrorCode.EXTERNAL_ERROR);
        }
        return !range.isEmpty();
    }

    private String getKey(String email) {
        return KEY_PREFIX + email;
    }

    private void removeExpiredInvites(String key, long now) {
        if (redisTemplate.opsForZSet().removeRangeByScore(key, 0, now) == null) {
            log.warn("파이프라인이나 트랜잭션 안에서 실행되었습니다.");
            throw new BusinessException(ErrorCode.EXTERNAL_ERROR);
        }
    }

    private void refreshKeyExpirationAtLatestInvite(String key) {
        Set<ZSetOperations.TypedTuple<String>> latestInvites = redisTemplate.opsForZSet() //가장 늦게 만료되는 초대 요청 가져오기
                .reverseRangeWithScores(key, 0, 0);

        if (latestInvites == null || latestInvites.isEmpty()) return;

        Double latestExpiresAt = latestInvites.iterator().next().getScore(); // 가장 늦게 끝나는 초대 요청의 만료 시간 가져오기
        if (latestExpiresAt == null) return;

        redisTemplate.expireAt(key, Date.from(Instant.ofEpochMilli(latestExpiresAt.longValue()))); // key를 해당 시간에 만료되도록 설정
        // 그러면 마지막 요청이 만료될때 zset 키 자체도 같이 만료되어서 삭제됨
    }
}
