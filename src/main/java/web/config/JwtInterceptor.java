package web.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import web.util.JwtUtil;

/**
 * JWT 토큰 인증 인터셉터
 * - 모든 요청에서 JWT 토큰을 검증
 * - 유효한 토큰이 있으면 요청 통과, 없으면 401 에러 반환
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    /**
     * Controller 실행 전에 호출되는 메서드
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param handler 요청 핸들러
     * @return true: 요청 계속 진행, false: 요청 중단
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. OPTIONS 메서드는 통과 (CORS preflight)
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 2. Authorization 헤더에서 토큰 추출
        String authHeader = request.getHeader("Authorization");

        // 3. Authorization 헤더가 없거나 Bearer로 시작하지 않으면 에러
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"토큰이 없습니다.\"}");
            return false;
        }

        // 4. Bearer 제거 후 토큰만 추출
        String token = jwtUtil.extractTokenFromBearer(authHeader);

        // 5. 토큰 유효성 검증
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"유효하지 않거나 만료된 토큰입니다.\"}");
            return false;
        }

        // 6. Access Token 타입 확인
        if (!jwtUtil.isAccessToken(token)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 403
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"Access Token이 아닙니다.\"}");
            return false;
        }

        // 7. 토큰에서 사용자 정보 추출하여 request에 저장
        // Controller에서 사용자 정보를 쉽게 가져올 수 있도록
        Integer userId = jwtUtil.getUserIdFromToken(token);
        String email = jwtUtil.getEmailFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);

        request.setAttribute("userId", userId);
        request.setAttribute("email", email);
        request.setAttribute("role", role);

        // 8. 요청 계속 진행
        return true;
    }
}