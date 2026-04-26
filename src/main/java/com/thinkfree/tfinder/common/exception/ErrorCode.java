package com.thinkfree.tfinder.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드
 * A - Authentication : 인증이나 권한 인가 중 발생한 에러
 * I - Invite : 초대 중 발생한 에러
 * E - Error : 공통으로 발생하는 에러
 * R - Request : 요청 에러
 */
@Getter
public enum ErrorCode {

    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "A-001"),
    AUTHORIZATION_FAILED(HttpStatus.FORBIDDEN, "A-002"),
    ACCESS_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "A-003"),
//    PASSWORD_UNMATCHED(HttpStatus.UNAUTHORIZED, "A-004"),
    REFRESH_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "A-005"),
    ACCESS_TOKEN_EXPIRED_ERROR(HttpStatus.UNAUTHORIZED, "A-006"),
    REFRESH_TOKEN_EXPIRED_ERROR(HttpStatus.UNAUTHORIZED, "A-007"),

    INVITE_TOKEN_ERROR(HttpStatus.BAD_REQUEST,"I-001"),
    SIGNUP_FIRST(HttpStatus.SEE_OTHER, "I-002"),
//    DUPLICATE_WORKSPACE_MEMBER(HttpStatus.CONFLICT, "I-003"),
    INVALID_WORKSPACE(HttpStatus.BAD_REQUEST, "I-004"),
    TOO_MANY_INVITE(HttpStatus.BAD_REQUEST, "I-005"),

    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "E-001"),
    DUPLICATE_ERROR(HttpStatus.CONFLICT, "E-002"),

    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "R-001"), ;

    private final HttpStatus status;
    private final String errorCode;

    ErrorCode(HttpStatus status, String errorCode) {
        this.status = status;
        this.errorCode = errorCode;
    }


}
