package com.thinkfree.tfinder.common.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
public class ErrorResponse {

    private final int status;
    private final String error;
    private final String code;

    public static ResponseEntity<ErrorResponse> toEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatusCode().value())
                .body(new ErrorResponse(
                        errorCode.getStatusCode().value(),
                        errorCode.getStatusCode().getReasonPhrase(),
                        errorCode.getCode()
                ));
    }

}
