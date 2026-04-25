package com.thinkfree.tfinder.auth.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class RefreshCookieProperties {

    @Value("${auth.refresh-cookie.name}")
    private String name;

    @Value("${auth.refresh-cookie.http-only}")
    private boolean httpOnly;

    @Value("${auth.refresh-cookie.secure}")
    private boolean secure;

    @Value("${auth.refresh-cookie.same-site}")
    private String sameSite;

    @Value("${auth.refresh-cookie.path}")
    private String path;

    @Value("${spring.jwt.expiration.refresh}")
    private long expirationSeconds;

}
