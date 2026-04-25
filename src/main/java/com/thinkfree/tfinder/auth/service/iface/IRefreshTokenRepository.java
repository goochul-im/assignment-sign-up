package com.thinkfree.tfinder.auth.service.iface;

import java.time.Duration;
import java.util.Optional;

public interface IRefreshTokenRepository {

    /**
     * 발급된 토큰을 저장합니다.
     * @param email 리프레쉬 토큰 발급자 이메일
     * @param refreshToken 발급된 토큰
     * @param expiration 만료 시간
     */
    void save(String email, String refreshToken, Duration expiration);

    /**
     * redis에 저장된 값을 가져옵니다.
     * @param email 리프레쉬 토큰 발급자 이메일
     * @return 발급된 토큰 값, nullable합니다.
     */
    Optional<String> findByEmail(String email);

    /**
     * redis에 저장된 토큰을 삭제합니다.
     * @param email 리프레쉬 토큰 발급자 이메일
     */
    void deleteByEmail(String email);
}
