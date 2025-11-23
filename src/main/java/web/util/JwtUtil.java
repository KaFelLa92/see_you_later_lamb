package web.util;

import org.springframework.stereotype.Component;

/**
 * JWT 토큰 생성 및 검증을 담당하는 유틸리티 클래스
 * - Access Token: 짧은 유효기간 (1시간), API 요청에 사용
 * - Refresh Token: 긴 유효기간 (7일), Access Token 재발급에 사용
 */

@Component
public class JwtUtil {

    // ============================================
    // [1] 설정값 (application.properties에서 주입)
    // ============================================


}
