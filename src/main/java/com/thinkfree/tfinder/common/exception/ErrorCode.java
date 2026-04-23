package com.thinkfree.tfinder.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    AUTHORIZED_FAILED(HttpStatus.UNAUTHORIZED, "A-001"),
    AUTHENTICATION_FAILED(HttpStatus.FORBIDDEN, "A-002"),
    ACCESS_TOKEN_ERROR(HttpStatus.BAD_REQUEST, "A-003"),
    PASSWORD_UNMATCHED(HttpStatus.BAD_REQUEST, "A-004"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "A-005"),

    INVITE_TOKEN_ERROR(HttpStatus.BAD_REQUEST,"I-001"),
    SIGNUP_FIRST(HttpStatus.SEE_OTHER, "I-002"),

    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "E-001"),
    ;

    private final HttpStatus status;
    private final String code;

    ErrorCode(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }


}
