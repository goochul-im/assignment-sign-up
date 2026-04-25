package com.thinkfree.tfinder.auth.controller;

import com.thinkfree.tfinder.auth.config.RefreshCookieProperties;
import com.thinkfree.tfinder.auth.controller.request.LoginRequest;
import com.thinkfree.tfinder.auth.controller.request.SignupRequest;
import com.thinkfree.tfinder.auth.controller.response.AccessTokenResponse;
import com.thinkfree.tfinder.auth.service.dto.LoginDto;
import com.thinkfree.tfinder.auth.service.dto.LoginResultDto;
import com.thinkfree.tfinder.auth.service.dto.SignupDto;
import com.thinkfree.tfinder.auth.service.iface.IAuthUseCase;
import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final IAuthUseCase authUseCase;
    private final RefreshCookieProperties refreshCookieProperties;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){

        LoginResultDto result = authUseCase.login(new LoginDto(
                request.email(),
                request.password()
        ));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createRefreshCookie(result.refreshToken()).toString())
                .body(new AccessTokenResponse(result.accessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            HttpServletRequest request
    ) {

        String refreshToken = extractRefreshToken(request);
        validateRefreshTokenCookie(refreshToken);
        LoginResultDto result = authUseCase.refresh(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createRefreshCookie(result.refreshToken()).toString())
                .body(new AccessTokenResponse(result.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request
    ) {

        String refreshToken = extractRefreshToken(request);
        validateRefreshTokenCookie(refreshToken);
        authUseCase.logout(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expireRefreshCookie().toString())
                .build();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request){

        authUseCase.signUp(new SignupDto(
                request.email(),
                request.username(),
                request.password()
        ));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseCookie createRefreshCookie(String refreshToken) {
        return ResponseCookie.from(refreshCookieProperties.getName(), refreshToken)
                .httpOnly(refreshCookieProperties.isHttpOnly())
                .secure(refreshCookieProperties.isSecure())
                .sameSite(refreshCookieProperties.getSameSite())
                .path(refreshCookieProperties.getPath())
                .maxAge(Duration.ofSeconds(refreshCookieProperties.getExpirationSeconds()))
                .build();
    }

    private ResponseCookie expireRefreshCookie() {
        return ResponseCookie.from(refreshCookieProperties.getName(), "")
                .httpOnly(refreshCookieProperties.isHttpOnly())
                .secure(refreshCookieProperties.isSecure())
                .sameSite(refreshCookieProperties.getSameSite())
                .path(refreshCookieProperties.getPath())
                .maxAge(Duration.ZERO)
                .build();
    }

    private void validateRefreshTokenCookie(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_ERROR);
        }
    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, refreshCookieProperties.getName());
        if (cookie == null) return null;

        return cookie.getValue();
    }
}
