package com.thinkfree.tfinder.auth.infrastructure.persistence.iface;

import com.thinkfree.tfinder.common.exception.BusinessException;
import org.springframework.data.redis.RedisConnectionFailureException;

import java.time.Duration;
import java.util.Optional;

public interface IRefreshTokenRepository {

    /**
     * 발급된 토큰을 저장합니다.
     * @param email 리프레쉬 토큰 발급자 이메일
     * @param refreshToken 발급된 토큰
     * @param expiration 만료 시간
     * @return 토큰이 저장되었을 경우 true, 파리미터로 null값이 들어와 저장되지 않았으면 false.
     * @exception RedisConnectionFailureException redis 연결이 끊어졌을 때 발생
     */
    boolean save(String email, String refreshToken, Duration expiration) throws RedisConnectionFailureException;

    /**
     * redis에 저장된 값을 가져옵니다. 해당 email의 리프레쉬 토큰 값이 없으면 null을 반환합니다.
     * @param email 리프레쉬 토큰 발급자 이메일
     * @return 발급된 토큰 값, nullable합니다.
     * @exception RedisConnectionFailureException redis 연결이 끊어졌을 때 발생
     */
    Optional<String> findByEmail(String email) throws RedisConnectionFailureException;

    /**
     * redis에 저장된 토큰을 삭제합니다.
     * @param email 리프레쉬 토큰 발급자 이메일
     * @return 값이 삭제되었으면 true, 아니면 false
     * @exception RedisConnectionFailureException redis 연결이 끊어졌을 때 발생
     */
    boolean deleteByEmail(String email) throws RedisConnectionFailureException;
}
