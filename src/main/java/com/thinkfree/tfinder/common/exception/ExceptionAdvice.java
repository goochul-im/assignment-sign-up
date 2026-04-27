package com.thinkfree.tfinder.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> businessExceptionHandler(BusinessException exception) {
        return ErrorResponse.toEntity(exception.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentExceptionHandler(MethodArgumentNotValidException exception) {
        log.warn(exception.getMessage());
        BindingResult bindingResult = exception.getBindingResult();

        HashMap<String, String> errorCause = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            if (error.getDefaultMessage() != null)
                errorCause.put(error.getField(), error.getDefaultMessage());
        }

        return ErrorResponse.toEntity(ErrorCode.INVALID_ARGUMENT, errorCause);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception
    ) {
        log.warn("json 파싱이 실패했습니다.");
        HashMap<String, String> errorCause = new HashMap<>();
        errorCause.put("body", "RequestBody 형식이 맞지 않습니다.");
        errorCause.put("errorMessage", exception.getMessage());

        return ErrorResponse.toEntity(ErrorCode.INVALID_REQUEST_BODY, errorCause);
    }

}
