package com.thinkfree.tfinder.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {

    INVITE_TOKEN_ERROR(BAD_REQUEST,"I-001"),
    SIGNUP_FIRST(SEE_OTHER, "I-002"),

    ENTITY_NOT_FOUND(NOT_FOUND, "E-001"),

    ACCESS_TOKEN_ERROR(BAD_REQUEST, "A-001"),
    PASSWORD_UNMATCHED(BAD_REQUEST, "A-002"),
    DUPLICATE_EMAIL(CONFLICT, "A-003");

    private final HttpStatus statusCode;
    private final String code;

    ErrorCode(HttpStatus statusCode, String code) {
        this.statusCode = statusCode;
        this.code = code;
    }


}
