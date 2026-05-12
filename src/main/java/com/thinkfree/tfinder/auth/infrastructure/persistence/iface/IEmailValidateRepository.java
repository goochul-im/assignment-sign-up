package com.thinkfree.tfinder.auth.infrastructure.persistence.iface;

import java.time.Duration;

public interface IEmailValidateRepository {

    /**
     * 이메일 인증 정보를 "대기중" 상태로 저장합니다.
     * @param email 인증 메일을 요청한 이메일
     * @param expiration 인증 대기 정보 유지 시간
     */
    void save(String email, Duration expiration);

    /**
     * 저장된 인증 정보 상태를 "인증됨"상태로 저장합니다
     * @param email 인증됨 상태로 저장될 이메일
     * @param expiration 인증됨 상태 유지 시간
     */
    void saveAsValidated(String email, Duration expiration);

    /**
     * 인증 정보가 있는지 확인하고 가져옵니다.
     * PENDING인지, VALIDATE인지 이 결과로는 알 수 없습니다.
     * 인증 정보가 없다면 null을 반환합니다.
     * @param email
     * @return 인증 정보
     */
    String getByEmail(String email);

    /**
     * 이메일 인증이 요청되어 요청 정보가 존재하는지 확인합니다.
     * 인증 정보가 없거나, VALIDATE 상태라면 false를 반환합니다.
     * @param email 요청되었는지 확인할 이메일
     * @return 요청되었는지 여부
     */
    boolean isRequested(String email);

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
