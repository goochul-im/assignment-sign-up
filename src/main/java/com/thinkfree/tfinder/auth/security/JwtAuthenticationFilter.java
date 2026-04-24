package com.thinkfree.tfinder.auth.security;

import com.thinkfree.tfinder.common.service.dto.AccessTokenResult;
import com.thinkfree.tfinder.common.service.iface.IJwtManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final IJwtManager jwtManager;
    private final CustomUserDetailsService customUserDetailsService;

    private final String AUTHORIZATION_HEADER = "Authorization";
    private final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = extractTokenInRequest(request);

        if (token != null) {

            AccessTokenResult tokenResult = jwtManager.parsingAccessToken(token);

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(tokenResult.email());

            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, null);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        }


        filterChain.doFilter(request, response);
    }

    private String extractTokenInRequest(HttpServletRequest request) {
        String bearerHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerHeader == null || !bearerHeader.startsWith(BEARER_PREFIX)) return null;

        return bearerHeader.substring(BEARER_PREFIX.length());
    }

}
