package com.backend.global.security.handler;

import com.backend.global.response.ErrorCode;
import com.backend.global.security.JwtErrorResponseWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증은 되었지만 권한(Role)이 없는 사용자가 접근할 때 (403 Forbidden)
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final JwtErrorResponseWriter jwtErrorResponseWriter;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        log.warn("접근 권한이 없는 요청입니다. URI: {}", request.getRequestURI());
        jwtErrorResponseWriter.write(response, ErrorCode.ACCESS_DENIED);
    }
}
