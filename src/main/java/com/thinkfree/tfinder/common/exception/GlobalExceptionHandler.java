package com.thinkfree.tfinder.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> businessExceptionHandler(BusinessException exception) {
        return ErrorResponse.toEntity(exception.getErrorCode(), Optional.ofNullable(exception.getMessage()));
    }

    @ExceptionHandler(SignupRequireException.class)
    public ResponseEntity<ErrorResponse> signupRequireExceptionHandler(SignupRequireException exception) {
        return ErrorResponse.toEntity(ErrorCode.SIGNUP_FIRST, Optional.ofNullable(exception.getMessage()));
    }

    @ExceptionHandler({RedisConnectionFailureException.class, RedisSystemException.class})
    public ResponseEntity<ErrorResponse> handleRedisConnectionFailureException(
            Exception exception
    ) {

        log.error("Redis 연결이 끊어졌습니다.");
        HashMap<String, String> errorCause = new HashMap<>();
        errorCause.put("errorMessage", exception.getMessage());

        return ErrorResponse.toEntity(ErrorCode.EXTERNAL_ERROR, errorCause);
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

//    @ExceptionHandler(RedisSystemException.class)
//    public ResponseEntity<ErrorResponse> handleRedisSystemException(
//            RedisSystemException exception
//    ) {
//
//        log.error("Redis 연결이 끊어졌습니다.");
//        HashMap<String, String> errorCause = new HashMap<>();
//        errorCause.put("errorMessage", exception.getMessage());
//
//        return ErrorResponse.toEntity(ErrorCode.EXTERNAL_ERROR, errorCause);
//    }

}
