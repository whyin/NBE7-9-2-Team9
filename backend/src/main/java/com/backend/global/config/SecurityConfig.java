package com.backend.global.config;

import com.backend.global.security.JwtAuthenticationFilter;
import com.backend.global.security.handler.JwtAccessDeniedHandler;
import com.backend.global.security.handler.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Spring Security의 전반적인 설정을 담당하는 클래스
 * 1. 인증(Authentication) - JWT 필터에서 검증
 * 2. 인가(Authorization) - 역할(Role)에 따른 접근 제어
 * 3. 세션 관리 - JWT는 Stateless(세션 비사용)
 * 4. 예외 처리 - 401 / 403 에러 핸들러 연결
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean

    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                // 0. 쿠키 포함 필수
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowCredentials(true); // 쿠키 포함 허용
                    config.addAllowedOriginPattern("http://localhost:3000"); // 프론트 주소
                    config.addAllowedHeader("*");
                    config.addAllowedMethod("*");
                    config.addExposedHeader("Set-Cookie"); // 쿠키 노출 허용
                    return config;
                }))

                // 1. 기본 보안 설정 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 2. 세션 관리 정책 - 세션 생성, 사용 X
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. 요청별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth

                        // 1. 인증이 필요 없는 요청
                        .requestMatchers(
                                "/api/members/signup",
                                "/api/auth/**",
                                "/h2-console/**"
                        ).permitAll()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        //TODO: 수정해야 함

                        // 4. 스웨거 관련 열어주기
                        .requestMatchers(
                                "/swagger-ui/",
                                "/swagger-ui/index.html",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()

                )
                // 4. 예외 처리 (401, 403)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 인증 실패
                        .accessDeniedHandler(jwtAccessDeniedHandler) // 인가 실패
                )

                // 5. jwt 필터 추가 (UsernamePasswordAuthenticationFilter 앞에 삽입)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 6. H2 콘솔 접근 허용
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
