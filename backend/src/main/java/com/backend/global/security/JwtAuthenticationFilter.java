package com.backend.global.security;

import com.backend.global.jwt.JwtTokenProvider;
import com.backend.global.jwt.TokenStatus;
import com.backend.global.response.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtErrorResponseWriter jwtErrorResponseWriter;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 1. 인증이 필요 없는 경로 (회원가입, 로그인, 재발급 등)
        if (List.of("/api/auth/login", "/api/members/signup", "/api/auth/reissue").contains(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Authorization 헤더에서 JWT 추출
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = (header != null && header.startsWith("Bearer "))
                ? header.substring(7).trim()
                : null;

        // 3. 토큰이 없으면 다음 필터로 (비로그인 요청)
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 4. 토큰 상태 검증
            TokenStatus status = jwtTokenProvider.validateTokenStatus(token);

            switch (status) {
                case VALID -> {

                    String tokenType = jwtTokenProvider.getTokenType(token);
                    if (!"access".equals(tokenType)) {
                        log.warn("[JWT] Refresh Token으로 접근 시도 차단: {}", requestURI);
                        jwtErrorResponseWriter.write(response, ErrorCode.INVALID_ACCESS_TOKEN);
                        return;
                    }

                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("[JWT] 유효한 토큰으로 인증 완료: {}", requestURI);
                }

                case EXPIRED -> {
                    log.warn("[JWT] 만료된 토큰입니다. 요청 URI: {}", requestURI);
                    jwtErrorResponseWriter.write(response, ErrorCode.EXPIRED_ACCESS_TOKEN);
                    return; // 다음 필터로 넘기지 않음
                }

                case INVALID -> {
                    log.warn("[JWT] 유효하지 않은 토큰입니다. 요청 URI: {}", requestURI);
                    jwtErrorResponseWriter.write(response, ErrorCode.INVALID_ACCESS_TOKEN);
                    return;
                }
            }

            // 5. 다음 필터로 요청 전달
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("[JWT] 필터 처리 중 예외 발생: {}", e.getMessage());
            jwtErrorResponseWriter.write(response, ErrorCode.UNAUTHORIZED_REQUEST);
        }
    }
}
