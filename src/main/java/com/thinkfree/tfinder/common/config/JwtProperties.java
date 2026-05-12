package com.thinkfree.tfinder.common.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@NoArgsConstructor
@Component
public class JwtProperties {

    private String key;
    private long accessExpirationSeconds;
    private long refreshExpirationSeconds;
    private long inviteExpirationSeconds;
    private long validateEmailExpirationSeconds;

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
