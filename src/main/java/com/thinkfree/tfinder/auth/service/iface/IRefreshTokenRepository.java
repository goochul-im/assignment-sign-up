package com.thinkfree.tfinder.auth.service.iface;

import java.time.Duration;
import java.util.Optional;

public interface IRefreshTokenRepository {

    void save(String email, String refreshToken, Duration expiration);

    Optional<String> findByEmail(String email);

    void deleteByEmail(String email);
}
