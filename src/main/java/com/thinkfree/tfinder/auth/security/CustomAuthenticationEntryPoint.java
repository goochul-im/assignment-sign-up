package com.thinkfree.tfinder.auth.security;

import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import com.thinkfree.tfinder.common.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {


        Map<String, String> errorResponse = null;
        BusinessException exception = (BusinessException) request.getAttribute("exception");

        if (exception != null) {
            log.warn("jwt 토큰이 만료되었습니다.");
            response.setStatus(exception.getErrorCode().getStatus().value());
            errorResponse = ErrorResponse.toSecurityErrorResponse(exception.getErrorCode());
        } else {
            log.warn("인증되지 않은 사용자입니다");
            errorResponse = ErrorResponse.toSecurityErrorResponse(ErrorCode.AUTHENTICATION_FAILED);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }

        String body = objectMapper.writeValueAsString(errorResponse);


        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(body);

    }

}
