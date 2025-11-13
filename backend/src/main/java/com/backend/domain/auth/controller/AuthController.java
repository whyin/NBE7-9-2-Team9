package com.backend.domain.auth.controller;

import com.backend.domain.auth.dto.reponse.TokenResponse;
import com.backend.domain.auth.service.AuthService;
import com.backend.domain.auth.util.CookieManager;
import com.backend.domain.member.dto.request.MemberLoginRequest;
import com.backend.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CookieManager cookieManager;

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@RequestBody MemberLoginRequest request,
                                            HttpServletResponse response) {
        TokenResponse tokenResponse = authService.login(request.memberId(), request.password());

        cookieManager.addRefreshTokenCookie(
                response,
                tokenResponse.refreshToken(),
                tokenResponse.refreshTokenMaxAge()
        );
        return ApiResponse.created(tokenResponse);
    }

    //TODO: 토큰을 헤더로 보낼지, 바디로 보낼지 결정
    @PostMapping("/reissue")
    public ApiResponse<TokenResponse> reissue(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        TokenResponse tokenResponse = authService.reissue(refreshToken);

        cookieManager.addRefreshTokenCookie(
                response,
                tokenResponse.refreshToken(),
                tokenResponse.refreshTokenMaxAge()
        );

        return ApiResponse.created(tokenResponse);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @RequestHeader("Authorization") String accessToken,
            HttpServletResponse response)
    {
        authService.logout(accessToken);
        cookieManager.deleteRefreshTokenCookie(response);
        return ApiResponse.created(null);
    }
}
