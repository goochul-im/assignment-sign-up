package com.thinkfree.tfinder.common.exception;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * 에러 응답을 위한 레코드
 * @param httpStatus HTTP 코드
 * @param reasonPhrase 에러 설명
 * @param errorCode 커스텀 에러 코드, ErrorCode 참고
 * @param cause 에러 발생 상세 이유
 */
public record ErrorResponse(
        int httpStatus,
        String reasonPhrase,
        String errorCode,
        Map<String, String> cause
) {

    public static ResponseEntity<ErrorResponse> toEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus().value())
                .body(new ErrorResponse(
                        errorCode.getStatus().value(),
                        errorCode.getStatus().getReasonPhrase(),
                        errorCode.getErrorCode(),
                        null
                ));
    }

    public static Map<String, String> toSecurityErrorResponse(ErrorCode errorCode) {
        HashMap<String, String> map = new HashMap<>();
        map.put("status", String.valueOf(errorCode.getStatus().value()));
        map.put("error", errorCode.getStatus().getReasonPhrase());
        map.put("code", errorCode.getErrorCode());
        return map;
    }

    public static ResponseEntity<ErrorResponse> toEntity(ErrorCode errorCode, Map<String, String> cause) {
        return ResponseEntity
                .status(errorCode.getStatus().value())
                .body(new ErrorResponse(
                        errorCode.getStatus().value(),
                        errorCode.getStatus().getReasonPhrase(),
                        errorCode.getErrorCode(),
                        cause
                ));
    }

}
