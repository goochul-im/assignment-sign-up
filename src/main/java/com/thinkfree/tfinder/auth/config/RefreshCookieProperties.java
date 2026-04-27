package com.thinkfree.tfinder.auth.config;

import com.thinkfree.tfinder.common.config.JwtProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class RefreshCookieProperties {

    private final JwtProperties jwtProperties;

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

    public long getExpirationSeconds() {
        return jwtProperties.getRefreshExpirationSeconds();
    }

}
