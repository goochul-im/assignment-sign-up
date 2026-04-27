package com.thinkfree.tfinder.common.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtProperties {

    private final String key;
    private final long accessExpirationSeconds;
    private final long refreshExpirationSeconds;
    private final long inviteExpirationSeconds;
    private final long validateEmailExpirationSeconds;

    public JwtProperties(
            @Value("${spring.jwt.key}") String key,
            @Value("${spring.jwt.expiration.access}") long accessExpirationSeconds,
            @Value("${spring.jwt.expiration.refresh}") long refreshExpirationSeconds,
            @Value("${spring.jwt.expiration.invite}") long inviteExpirationSeconds,
            @Value("${spring.jwt.expiration.validate}") long validateEmailExpirationSeconds
    ) {
        this.key = key;
        this.accessExpirationSeconds = accessExpirationSeconds;
        this.refreshExpirationSeconds = refreshExpirationSeconds;
        this.inviteExpirationSeconds = inviteExpirationSeconds;
        this.validateEmailExpirationSeconds = validateEmailExpirationSeconds;
    }
}
