package web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.model.dto.user.UsersDto;
import web.service.AuthService;
import web.service.UserService;

import java.util.HashMap;
import java.util.Map;

/**
 * 인증 관련 API 컨트롤러
 * - 로그인, 회원가입, 토큰 재발급, 로그아웃 등
 * - JWT 인증이 필요 없는 공개 API들
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    // ============================================
    // [1] 회원가입
    // ============================================

    /**
     * 일반 회원가입
     * POST /api/auth/signup
     *
     * Request Body:
     * {
     *   "email": "user@example.com",
     *   "password": "password123",
     *   "user_name": "홍길동",
     *   "phone": "010-1234-5678",
     *   "addr": "서울시 강남구",
     *   "addr_detail": "123동 456호"
     * }
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody UsersDto usersDto) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. 이메일 중복 검사
            if (userService.checkEmail(usersDto.getEmail())) {
                response.put("success", false);
                response.put("message", "이미 사용 중인 이메일입니다.");
                return ResponseEntity.badRequest().body(response);
            }

            // 2. 연락처 중복 검사
            if (userService.checkPhone(usersDto.getPhone())) {
                response.put("success", false);
                response.put("message", "이미 사용 중인 연락처입니다.");
                return ResponseEntity.badRequest().body(response);
            }

            // 3. 회원가입 처리
            UsersDto savedUser = userService.signUp(usersDto);

            if (savedUser != null && savedUser.getUser_id() > 0) {
                response.put("success", true);
                response.put("message", "회원가입 성공");
                response.put("user", savedUser);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "회원가입 실패");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // ============================================
    // [2] 로그인
    // ============================================

    /**
     * 일반 로그인
     * POST /api/auth/login
     *
     * Request Body:
     * {
     *   "email": "user@example.com",
     *   "password": "password123"
     * }
     *
     * Response:
     * {
     *   "success": true,
     *   "accessToken": "eyJhbGciOiJIUzI1NiIs...",
     *   "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
     *   "user": { ... }
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        // 로그인 처리
        Map<String, Object> result = authService.login(email, password);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // ============================================
    // [3] 토큰 재발급
    // ============================================

    /**
     * Access Token 재발급
     * POST /api/auth/refresh
     *
     * Request Body:
     * {
     *   "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
     * }
     *
     * Response:
     * {
     *   "success": true,
     *   "accessToken": "eyJhbGciOiJIUzI1NiIs..."
     * }
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@RequestBody Map<String, String> tokenData) {
        String refreshToken = tokenData.get("refreshToken");

        // Access Token 재발급
        Map<String, Object> result = authService.refreshAccessToken(refreshToken);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // ============================================
    // [4] 로그아웃
    // ============================================

    /**
     * 로그아웃
     * POST /api/auth/logout
     *
     * Headers:
     * Authorization: Bearer <access_token>
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Authorization") String authHeader) {
        // Bearer 토큰에서 실제 토큰 추출
        String token = authHeader.substring(7);

        // 로그아웃 처리
        Map<String, Object> result = authService.logout(token);

        return ResponseEntity.ok(result);
    }

    // ============================================
    // [5] 소셜 로그인
    // ============================================

    /**
     * 소셜 로그인 (구글, 카카오, 네이버)
     * POST /api/auth/social-login
     *
     * Request Body:
     * {
     *   "email": "user@example.com",
     *   "userName": "홍길동",
     *   "signupType": 2  // 2: 구글, 3: 카카오, 4: 네이버
     * }
     */
    @PostMapping("/social-login")
    public ResponseEntity<Map<String, Object>> socialLogin(@RequestBody Map<String, Object> socialData) {
        String email = (String) socialData.get("email");
        String userName = (String) socialData.get("userName");
        Integer signupType = (Integer) socialData.get("signupType");

        // 소셜 로그인 처리
        Map<String, Object> result = authService.socialLogin(email, userName, signupType);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // ============================================
    // [6] 이메일/연락처 중복 확인
    // ============================================

    /**
     * 이메일 중복 확인
     * GET /api/auth/check-email?email=user@example.com
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();

        boolean exists = userService.checkEmail(email);

        response.put("exists", exists);
        response.put("message", exists ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다.");

        return ResponseEntity.ok(response);
    }

    /**
     * 연락처 중복 확인
     * GET /api/auth/check-phone?phone=010-1234-5678
     */
    @GetMapping("/check-phone")
    public ResponseEntity<Map<String, Object>> checkPhone(@RequestParam String phone) {
        Map<String, Object> response = new HashMap<>();

        boolean exists = userService.checkPhone(phone);

        response.put("exists", exists);
        response.put("message", exists ? "이미 사용 중인 연락처입니다." : "사용 가능한 연락처입니다.");

        return ResponseEntity.ok(response);
    }

    // ============================================
    // [7] 이메일/비밀번호 찾기
    // ============================================

    /**
     * 이메일 찾기
     * POST /api/auth/find-email
     *
     * Request Body:
     * {
     *   "userName": "홍길동",
     *   "phone": "010-1234-5678"
     * }
     */
    @PostMapping("/find-email")
    public ResponseEntity<Map<String, Object>> findEmail(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();

        String userName = data.get("userName");
        String phone = data.get("phone");

        String email = userService.findEmail(userName, phone);

        if (email != null) {
            response.put("success", true);
            response.put("email", email);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "일치하는 사용자 정보가 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 비밀번호 찾기 (임시 비밀번호 발급)
     * POST /api/auth/find-password
     *
     * Request Body:
     * {
     *   "email": "user@example.com",
     *   "phone": "010-1234-5678"
     * }
     */
    @PostMapping("/find-password")
    public ResponseEntity<Map<String, Object>> findPassword(@RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();

        String email = data.get("email");
        String phone = data.get("phone");

        String tempPassword = userService.findPassword(email, phone);

        if (tempPassword != null) {
            response.put("success", true);
            response.put("message", "임시 비밀번호가 발급되었습니다.");
            // 실제로는 이메일로 전송하고 비밀번호는 반환하지 않음
            response.put("tempPassword", tempPassword);  // 개발용
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "일치하는 사용자 정보가 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ============================================
    // [8] 토큰 검증 (프론트엔드에서 토큰 유효성 확인용)
    // ============================================

    /**
     * Access Token 검증
     * POST /api/auth/validate
     *
     * Request Body:
     * {
     *   "accessToken": "eyJhbGciOiJIUzI1NiIs..."
     * }
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestBody Map<String, String> tokenData) {
        String accessToken = tokenData.get("accessToken");

        Map<String, Object> result = authService.validateAccessToken(accessToken);

        return ResponseEntity.ok(result);
    }
}