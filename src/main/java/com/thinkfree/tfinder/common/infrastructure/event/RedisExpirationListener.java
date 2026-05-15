package com.thinkfree.tfinder.common.infrastructure.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisExpirationListener {

    @EventListener
    void handleRedisKeyExpired(RedisKeyExpiredEvent<?> event) {
        byte[] idBytes = event.getId();
        String key = idBytes.toString();
        System.out.println("handling");

        if (key.startsWith("test:")) {
            System.out.println("test complete");
        }

    }

}
