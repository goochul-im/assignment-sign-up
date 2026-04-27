package com.thinkfree.tfinder.auth.infrastructure.persistence.iface;

import java.time.Duration;

public interface IEmailValidateRepository {

    /**
     * 이메일 인증 정보를 저장합니다.
     * @param email 인증된 이메일
     * @param expiration 인증 정보 유지 시간
     */
    void save(String email, Duration expiration);

    /**
     * 이 이메일이 인증되었는지 확인합니다.
     * @param email 인증되었는지 확인할 이메일
     * @return 인증되었는지 여부
     */
    boolean isValidate(String email);

    /**
     * 이메일 인증 정보를 삭제합니다.
     * @param email 인증 정보를 삭제할 이메일
     */
    void delete(String email);

}
