package com.thinkfree.tfinder.auth.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IEmailSendLimitRepository;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisEmailSendLimitRepository implements IEmailSendLimitRepository {

    private final StringRedisTemplate template;
    private final Duration TTL = Duration.ofHours(1);
    private final String KEY_PREFIX = "send:limit:";

    @Override
    public int getRemainLimit(int mailLimit, long workspaceId) {
        Boolean hasLimit = template.opsForValue().setIfAbsent(key(workspaceId), String.valueOf(mailLimit), TTL);

        if (hasLimit) return mailLimit;
        return Integer.valueOf(template.opsForValue().get(key(workspaceId)));
    }

    @Override
    public boolean decreaseRemainLimit(int decrease, long workspaceId) {
        Long remain = template.opsForValue().decrement(key(workspaceId), decrease);
        if (remain < 0) {
            template.opsForValue().increment(key(workspaceId), decrease); //복구
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT);
        }

        return true;
    }

    public String key(long workspaceId) {
        return KEY_PREFIX + workspaceId;
    }

}
