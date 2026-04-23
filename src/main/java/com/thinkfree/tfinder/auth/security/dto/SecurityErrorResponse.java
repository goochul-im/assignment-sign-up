package com.thinkfree.tfinder.auth.security.dto;


import org.springframework.http.HttpStatus;

public record SecurityErrorResponse(
    HttpStatus httpStatus,
    String errorMessage
){
}
