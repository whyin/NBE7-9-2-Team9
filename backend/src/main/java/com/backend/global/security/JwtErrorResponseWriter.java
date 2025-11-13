package com.backend.global.security;

import com.backend.global.response.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtErrorResponseWriter {

    private final ObjectMapper objectMapper;

    public void write(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorBody = Map.of(
                "code", errorCode.getCode(),
                "status", errorCode.getStatus().value(),
                "message", errorCode.getMessage()
        );

        objectMapper.writeValue(response.getWriter(), errorBody);
    }
}
