package com.thinkfree.tfinder.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public record ErrorResponse(
        int status,
        String error,
        String code,
        Map<String, String> cause
) {

    public static ResponseEntity<ErrorResponse> toEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus().value())
                .body(new ErrorResponse(
                        errorCode.getStatus().value(),
                        errorCode.getStatus().getReasonPhrase(),
                        errorCode.getCode(),
                        null
                ));
    }

    // TODO: 이렇게 만들어도 되는건지?
    public static Map<String, String> toSecurityErrorResponse(ErrorCode errorCode) {
        HashMap<String, String> map = new HashMap<>();
        map.put("status", String.valueOf(errorCode.getStatus().value()));
        map.put("error", errorCode.getStatus().getReasonPhrase());
        map.put("code", errorCode.getCode());
        return map;
    }

    public static ResponseEntity<ErrorResponse> toEntity(ErrorCode errorCode, Map<String, String> cause) {
        return ResponseEntity
                .status(errorCode.getStatus().value())
                .body(new ErrorResponse(
                        errorCode.getStatus().value(),
                        errorCode.getStatus().getReasonPhrase(),
                        errorCode.getCode(),
                        cause
                ));
    }

}
