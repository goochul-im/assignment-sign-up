package com.thinkfree.tfinder.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드
 * A - Authentication : 인증/가입 중 발생한 에러
 * I - Invite : 초대 중 발생한 에러
 * E - Error : 기타 에러
 * R - Request : 요청 에러
 */
@Getter
public enum ErrorCode {

    AUTHORIZED_FAILED(HttpStatus.UNAUTHORIZED, "A-001"),
    AUTHENTICATION_FAILED(HttpStatus.FORBIDDEN, "A-002"),
    ACCESS_TOKEN_ERROR(HttpStatus.BAD_REQUEST, "A-003"),
    PASSWORD_UNMATCHED(HttpStatus.BAD_REQUEST, "A-004"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "A-005"),

    INVITE_TOKEN_ERROR(HttpStatus.BAD_REQUEST,"I-001"),
    SIGNUP_FIRST(HttpStatus.SEE_OTHER, "I-002"),
    DUPLICATE_WORKSPACE_MEMBER(HttpStatus.CONFLICT, "I-003"),

    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "E-001"),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "R-001"), ;

    private final HttpStatus status;
    private final String errorCode;

    ErrorCode(HttpStatus status, String errorCode) {
        this.status = status;
        this.errorCode = errorCode;
    }


}
