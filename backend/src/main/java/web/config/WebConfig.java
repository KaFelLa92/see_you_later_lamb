package web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 설정
 * - JWT 인터셉터 등록
 * - CORS 설정 (React/Next.js, React Native에서 API 호출 허용)
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    /**
     * 인터셉터 등록
     * - 특정 경로에 대해 JWT 인증 적용/제외 설정
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // JWT 인증이 필요한 경로
                .addPathPatterns("/api/**")

                // JWT 인증에서 제외할 경로 (인증 없이 접근 가능)
                .excludePathPatterns(
                        "/api/auth/login",           // 로그인
                        "/api/auth/signup",          // 회원가입
                        "/api/auth/refresh",         // 토큰 재발급
                        "/api/auth/social-login",    // 소셜 로그인
                        "/api/auth/check-email",     // 이메일 중복 확인
                        "/api/auth/check-phone",     // 연락처 중복 확인
                        "/api/auth/find-email",      // 이메일 찾기
                        "/api/auth/find-password",   // 비밀번호 찾기
                        "/api/promise/share/**",     // 약속 공유 링크 (비회원 접근)
                        "/error",                    // 에러 페이지
                        "/swagger-ui/**",            // Swagger (API 문서)
                        "/v3/api-docs/**"            // Swagger
                );
    }

    /**
     * CORS 설정
     * - React/Next.js (PC 웹) 및 React Native (모바일)에서 API 호출 허용
     * - 실제 운영 환경에서는 allowedOrigins를 특정 도메인으로 제한해야 함
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // /api로 시작하는 모든 경로
                // 허용할 출처 (개발 환경)
                .allowedOrigins(
                        "http://localhost:3000",      // Next.js 개발 서버
                        "http://localhost:19006",     // React Native Expo
                        "http://192.168.*.*:3000",    // 같은 네트워크 내 모바일 기기
                        "http://192.168.*.*:19006"
                )
                // 허용할 HTTP 메서드
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                // 허용할 헤더
                .allowedHeaders("*")
                // 인증 정보 포함 허용 (쿠키, Authorization 헤더 등)
                .allowCredentials(true)
                // preflight 요청 캐시 시간 (초)
                .maxAge(3600);
    }
}