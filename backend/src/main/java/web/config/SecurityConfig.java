package web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정
 * - 인증/인가 규칙 설정
 * - JWT 필터 체인 구성
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Security 필터 체인 설정
     * - 어떤 URL은 인증 없이 접근 가능하게 할지 정의
     * - 어떤 URL은 인증이 필요한지 정의
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (REST API이므로)
                .csrf(csrf -> csrf.disable())

                // 인증/인가 규칙 설정
                .authorizeHttpRequests(auth -> auth
                        // /api/auth/** 경로는 인증 없이 접근 가능 (로그인, 회원가입 등)
                        .requestMatchers("/api/auth/**").permitAll()

                        // 개발 중에는 모든 API 허용 (테스트용)
                        .requestMatchers("/api/**").permitAll()

                        // 나머지 요청은 인증 필요
                        // .anyRequest().authenticated()

                        // 또는 모든 요청 허용 (개발 중)
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}