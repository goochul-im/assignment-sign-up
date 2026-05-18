package com.thinkfree.tfinder.auth.infrastructure.persistence.iface;

import java.time.Duration;

public interface IEmailValidateRepository {

    /**
     * 저장된 인증 정보 상태를 "인증됨"상태로 저장합니다
     * @param email 인증됨 상태로 저장될 이메일
     * @param expiration 인증됨 상태 유지 시간
     */
    void saveAsValidated(String email, Duration expiration);

    /**
     * 이 이메일이 인증되었는지 확인합니다.
     * 인증 정보가 없거나, PENDING 상태라면 false를 반환합니다.
     * @param email 인증되었는지 확인할 이메일
     * @return 인증되었는지 여부
     */
    boolean isValidated(String email);

    /**
     * 이메일 인증 정보를 삭제합니다.
     * @param email 인증 정보를 삭제할 이메일
     */
    boolean delete(String email);

}
