package com.thinkfree.tfinder.auth.infrastructure.persistence.adapter;

import com.thinkfree.tfinder.auth.infrastructure.persistence.iface.IEmailSendLimitRepository;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisEmailSendLimitRepository implements IEmailSendLimitRepository {

    private final StringRedisTemplate template;
    private final Duration INITIAL_TIME = Duration.ofHours(1);
    private final String KEY_PREFIX = "send:limit:";

    @Override
    public int getRemainLimit(int mailLimit, long workspaceId) {
        Boolean hasLimit = template.opsForValue().setIfAbsent(key(workspaceId), String.valueOf(mailLimit), INITIAL_TIME);
        if (hasLimit == null) throw new BusinessException(ErrorCode.EXTERNAL_ERROR, "redis 트랜잭션이나 파이프라인 안에서 실행되었습니다");

        if (hasLimit) return mailLimit;
        return Integer.valueOf(template.opsForValue().get(key(workspaceId)));
    }

    public long getLimitTTL(long workspaceId) {
        return template.getExpire(key(workspaceId), TimeUnit.MINUTES);
    }

    public String key(long workspaceId) {
        return KEY_PREFIX + workspaceId;
    }

}
