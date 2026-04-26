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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "회원 인증 및 토큰 재발급", description = "액세스 토큰과 토큰을 재발급받는 API")
public class AuthController {

    private final IAuthUseCase authUseCase;
    private final RefreshCookieProperties refreshCookieProperties;

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인합니다. 액세스 토큰은 body로, 리프레쉬 토큰은 쿠키로 발급받습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공", content =
            @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AccessTokenResponse.class)
            )),
            @ApiResponse(responseCode = "401", description = "A-001, 이메일 또는 비밀번호 불일치"),
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        LoginResultDto result = authUseCase.login(new LoginDto(
                request.email(),
                request.password()
        ));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createRefreshCookie(result.refreshToken()).toString())
                .body(new AccessTokenResponse(result.accessToken()));
    }

    @Operation(
            summary = "토큰 재발급",
            description = "액세스 토큰과 리프레쉬 토큰을 재발급합니다. 액세스 토큰은 body로 리프레쉬 토큰은 쿠키로 발급됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공", content =
            @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AccessTokenResponse.class)
            )
            ),
            @ApiResponse(responseCode = "401", description = "A-005, 리프레쉬 토큰에 오류가 있음"),
    })
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

    @Operation(
            summary = "로그아웃",
            description = "멤버 로그아웃을 진행합니다. 이 API를 호출한 이후 리프레쉬 토큰은 무효화되지만, " +
                    "액세스 토큰은 직접 삭제를 진행해야합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "A-005, 리프레쉬 토큰에 오류가 있음"),
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request
    ) {

        String refreshToken = extractRefreshToken(request);
        validateRefreshTokenCookie(refreshToken);
        authUseCase.logout(refreshToken);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, expireRefreshCookie().toString())
                .build();
    }

    @Operation(
            summary = "회원가입",
            description = "회원가입을 진행합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "409", description = "E-002, 중복 이메일"),
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {

        authUseCase.signUp(new SignupDto(
                request.email(),
                request.nickname(),
                request.password()
        ));

        return ResponseEntity.noContent()
                .build();
    }

    private ResponseCookie createRefreshCookie(String refreshToken) {
        return ResponseCookie.from(refreshCookieProperties.getName(), refreshToken)
                .httpOnly(refreshCookieProperties.isHttpOnly())
                .secure(refreshCookieProperties.isSecure())
                .sameSite(refreshCookieProperties.getSameSite())
                .path(refreshCookieProperties.getPath())
                .maxAge(refreshCookieProperties.getExpirationSeconds())
                .build();
    }

    private ResponseCookie expireRefreshCookie() {
        return ResponseCookie.from(refreshCookieProperties.getName(), "")
                .httpOnly(refreshCookieProperties.isHttpOnly())
                .secure(refreshCookieProperties.isSecure())
                .sameSite(refreshCookieProperties.getSameSite())
                .path(refreshCookieProperties.getPath())
                .maxAge(0)
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
