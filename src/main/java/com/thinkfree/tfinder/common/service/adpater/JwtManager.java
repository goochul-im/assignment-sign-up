package com.thinkfree.tfinder.common.service.adpater;

import com.thinkfree.tfinder.common.exception.BusinessException;
import com.thinkfree.tfinder.common.service.dto.AccessTokenResult;
import com.thinkfree.tfinder.common.service.iface.IJwtManager;
import com.thinkfree.tfinder.common.service.dto.InviteTokenResult;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.thinkfree.tfinder.common.exception.ErrorCode.*;

@Component
public class JwtManager implements IJwtManager {

    private final SecretKey secretKey; // 서명을 위한 시크릿 키

    private final String FROM_EMAIL = "from_email";
    private final String TO_EMAIL = "to_email";
    private final String WORKSPACE_URL = "workspace_url";
    private final String INVITE_TOKEN_SUBJECT = "workspace_invite_token";

    private final String MEMBER_EMAIL = "member_email";
    private final String ACCESS_TOKEN_SUBJECT = "access_token";


    public JwtManager(@Value("${spring.jwt.key}") String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    @Override
    public String generateInviteToken(String fromEmail, String toEmail, String workspaceUrl, Instant expirationTime) {
        Map<String, String> claims = new HashMap<>();
        claims.put(FROM_EMAIL, fromEmail);
        claims.put(WORKSPACE_URL, workspaceUrl);
        claims.put(TO_EMAIL, toEmail);

        return produceJwt(INVITE_TOKEN_SUBJECT, expirationTime, claims);
    }

    @Override
    public String generateAccessToken(String memberEmail, Instant expirationTime) {
        HashMap<String, String > claims = new HashMap<>();
        claims.put(MEMBER_EMAIL, memberEmail);
        return produceJwt(ACCESS_TOKEN_SUBJECT, expirationTime, claims);
    }

    @Override
    public InviteTokenResult parsingInviteToken(String token) {
        Claims claims;

        try {

            claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (!claims.getSubject().equals(INVITE_TOKEN_SUBJECT))
                throw new JwtException("this token isn't for invite");

        } catch (JwtException e) {
            throw new BusinessException(e.getMessage(), INVITE_TOKEN_ERROR);
        }

        String fromEmail = (String) claims.get(FROM_EMAIL);
        String workspaceUrl = (String) claims.get(WORKSPACE_URL);
        String toEmail = (String) claims.get(TO_EMAIL);

        return new InviteTokenResult(
                toEmail,
                fromEmail,
                workspaceUrl
        );
    }

    @Override
    public AccessTokenResult parsingAccessToken(String token) {
        Claims claims;

        try {

            claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (!claims.getSubject().equals(ACCESS_TOKEN_SUBJECT))
                throw new JwtException("this token isn't for accessToken");

        } catch (JwtException e) {
            throw new BusinessException(e.getMessage(), ACCESS_TOKEN_ERROR);
        }

        String memberEmail = (String) claims.get(MEMBER_EMAIL);

        return new AccessTokenResult(
                memberEmail
        );
    }

    private String produceJwt(String subject, Instant expirationTime, Map<String, ?> claims) {

        return Jwts.builder()
                .expiration(Date.from(expirationTime))
                .subject(subject)
                .claims(claims)
                .signWith(secretKey)
                .compact();
    }

}
