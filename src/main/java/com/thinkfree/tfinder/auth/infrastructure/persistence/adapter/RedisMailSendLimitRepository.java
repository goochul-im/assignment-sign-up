package com.thinkfree.tfinder.auth.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IMailSendLimitRepository;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisMailSendLimitRepository implements IMailSendLimitRepository {

    private final StringRedisTemplate template;
    private final Duration TTL = Duration.ofHours(1);
    private final String KEY_PREFIX = "send:limit:";

    @Override
    public int getRemainLimit(int mailLimit, long workspaceId) {
        if (template.opsForValue().setIfAbsent(getKey(workspaceId), String.valueOf(mailLimit), TTL) == null) {
            log.warn("파이프라인이나 트랜잭션 안에서 실행되었습니다.");
            throw new BusinessException(ErrorCode.EXTERNAL_ERROR);
        }
        return getLimit(workspaceId);
    }

    @Override
    public boolean decreaseRemainLimit(int decrease, long workspaceId) {
        Long remain = template.opsForValue().decrement(getKey(workspaceId));

        if (remain == null) {
            log.warn("파이프라인이나 트랜잭션 안에서 실행되었습니다.");
            throw new BusinessException(ErrorCode.EXTERNAL_ERROR);
        }

        if (remain < 0) {
            template.opsForValue().increment(getKey(workspaceId), decrease); // 복구 시퀀스
            return false;
        }
        return true;
    }

    @Override
    public void increaseRemainLimit(int increase, long workspaceId) {
        if (template.opsForValue().increment(getKey(workspaceId), increase) == null) {
            log.warn("파이프라인이나 트랜잭션 안에서 실행되었습니다.");
            throw new BusinessException(ErrorCode.EXTERNAL_ERROR);
        }
    }

    private int getLimit(long workspaceId) {
        String s = template.opsForValue().get(getKey(workspaceId));
        if (s == null) return 0;
        return Integer.parseInt(s);
    }

    public String getKey(long workspaceId) {
        return KEY_PREFIX + workspaceId;
    }

}
