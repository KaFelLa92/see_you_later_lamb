package web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.model.dto.user.UsersDto;
import web.model.entity.user.UsersEntity;
import web.repository.user.UsersRepository;
import web.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JWT 기반 인증 서비스
 * - 로그인, 토큰 재발급, 로그아웃 등 인증 관련 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    // ============================================
    // [*] DI (Dependency Injection)
    // ============================================
    private final UsersRepository usersRepository;
    private final JwtUtil jwtUtil;

    // ============================================
    // [1] 로그인 (JWT 토큰 발급)
    // ============================================

    /**
     * 로그인 처리 및 JWT 토큰 발급
     * @param email 이메일
     * @param password 비밀번호
     * @return 로그인 결과 Map (성공: 토큰 포함, 실패: 에러 메시지)
     */
    public Map<String, Object> login(String email, String password) {
        Map<String, Object> result = new HashMap<>();

        // 1. 이메일로 사용자 조회
        Optional<UsersEntity> userOptional = usersRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            // 사용자가 존재하지 않음
            result.put("success", false);
            result.put("message", "이메일 또는 비밀번호가 일치하지 않습니다.");
            return result;
        }

        UsersEntity user = userOptional.get();

        // 2. 비밀번호 검증 (실제로는 BCrypt 등으로 암호화된 비밀번호 비교)
        if (!user.getPassword().equals(password)) {
            result.put("success", false);
            result.put("message", "이메일 또는 비밀번호가 일치하지 않습니다.");
            return result;
        }

        // 3. 계정 상태 확인
        if (user.getUser_state() == -1) {
            result.put("success", false);
            result.put("message", "탈퇴한 계정입니다.");
            return result;
        }

        if (user.getUser_state() == 0) {
            result.put("success", false);
            result.put("message", "휴면 계정입니다. 계정 활성화가 필요합니다.");
            return result;
        }

        // 4. JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(
                user.getUser_id(),
                user.getEmail(),
                user.getRole().name()
        );

        String refreshToken = jwtUtil.generateRefreshToken(
                user.getUser_id(),
                user.getEmail()
        );

        // 5. 로그인 성공 결과 반환
        result.put("success", true);
        result.put("message", "로그인 성공");
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        result.put("user", user.toDto());  // 사용자 정보도 함께 반환

        return result;
    }

    // ============================================
    // [2] 토큰 재발급 (Refresh Token으로 Access Token 갱신)
    // ============================================

    /**
     * Refresh Token으로 새로운 Access Token 발급
     * @param refreshToken Refresh Token
     * @return 재발급 결과 Map
     */
    public Map<String, Object> refreshAccessToken(String refreshToken) {
        Map<String, Object> result = new HashMap<>();

        // 1. Refresh Token 유효성 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            result.put("success", false);
            result.put("message", "유효하지 않거나 만료된 Refresh Token입니다.");
            return result;
        }

        // 2. Refresh Token 타입 확인
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            result.put("success", false);
            result.put("message", "Refresh Token이 아닙니다.");
            return result;
        }

        // 3. Token에서 사용자 정보 추출
        Integer userId = jwtUtil.getUserIdFromToken(refreshToken);
        String email = jwtUtil.getEmailFromToken(refreshToken);

        // 4. 사용자 조회 (DB에서 최신 정보 확인)
        Optional<UsersEntity> userOptional = usersRepository.findById(userId);

        if (userOptional.isEmpty()) {
            result.put("success", false);
            result.put("message", "존재하지 않는 사용자입니다.");
            return result;
        }

        UsersEntity user = userOptional.get();

        // 5. 계정 상태 확인
        if (user.getUser_state() != 1) {
            result.put("success", false);
            result.put("message", "비활성 계정입니다.");
            return result;
        }

        // 6. 새로운 Access Token 생성
        String newAccessToken = jwtUtil.generateAccessToken(
                user.getUser_id(),
                user.getEmail(),
                user.getRole().name()
        );

        // 7. 재발급 성공 결과 반환
        result.put("success", true);
        result.put("message", "Access Token 재발급 성공");
        result.put("accessToken", newAccessToken);

        return result;
    }

    // ============================================
    // [3] 토큰 검증
    // ============================================

    /**
     * Access Token 검증 및 사용자 정보 조회
     * @param accessToken Access Token
     * @return 검증 결과 Map
     */
    public Map<String, Object> validateAccessToken(String accessToken) {
        Map<String, Object> result = new HashMap<>();

        // 1. 토큰 유효성 검증
        if (!jwtUtil.validateToken(accessToken)) {
            result.put("valid", false);
            result.put("message", "유효하지 않거나 만료된 토큰입니다.");
            return result;
        }

        // 2. Access Token 타입 확인
        if (!jwtUtil.isAccessToken(accessToken)) {
            result.put("valid", false);
            result.put("message", "Access Token이 아닙니다.");
            return result;
        }

        // 3. 토큰에서 사용자 정보 추출
        Integer userId = jwtUtil.getUserIdFromToken(accessToken);
        String email = jwtUtil.getEmailFromToken(accessToken);
        String role = jwtUtil.getRoleFromToken(accessToken);

        // 4. 검증 성공 결과 반환
        result.put("valid", true);
        result.put("userId", userId);
        result.put("email", email);
        result.put("role", role);

        return result;
    }

    /**
     * 토큰에서 사용자 정보 조회
     * @param accessToken Access Token
     * @return 사용자 정보 DTO (실패시 null)
     */
    public UsersDto getUserFromToken(String accessToken) {
        // 1. 토큰 검증
        if (!jwtUtil.validateToken(accessToken) || !jwtUtil.isAccessToken(accessToken)) {
            return null;
        }

        // 2. 사용자 ID 추출
        Integer userId = jwtUtil.getUserIdFromToken(accessToken);

        // 3. DB에서 사용자 조회
        Optional<UsersEntity> userOptional = usersRepository.findById(userId);

        // 4. DTO로 변환하여 반환
        return userOptional.map(UsersEntity::toDto).orElse(null);
    }

    // ============================================
    // [4] 로그아웃 (토큰 무효화)
    // ============================================

    /**
     * 로그아웃
     * JWT는 stateless이므로 서버에서 토큰을 삭제할 수 없음
     * 1. 클라이언트에서 토큰 삭제 (LocalStorage, AsyncStorage)
     * 2. (옵션) Redis 등에 블랙리스트로 등록하여 해당 토큰 사용 차단
     *
     * @param accessToken Access Token
     * @return 로그아웃 결과 Map
     */
    public Map<String, Object> logout(String accessToken) {
        Map<String, Object> result = new HashMap<>();

        // 1. 토큰 검증 (유효한 토큰인지만 확인)
        if (!jwtUtil.validateToken(accessToken)) {
            result.put("success", false);
            result.put("message", "유효하지 않은 토큰입니다.");
            return result;
        }

        // 2. (옵션) 토큰 블랙리스트 등록
        // Redis에 토큰을 저장하고, 만료 시간까지 블랙리스트로 유지
        // redisTemplate.opsForValue().set(
        //     "blacklist:" + accessToken,
        //     "logout",
        //     토큰_남은_유효시간,
        //     TimeUnit.MILLISECONDS
        // );

        // 3. 로그아웃 성공
        result.put("success", true);
        result.put("message", "로그아웃 성공");

        return result;
    }

    // ============================================
    // [5] 소셜 로그인 (구글, 카카오, 네이버)
    // ============================================

    /**
     * 소셜 로그인 처리
     * OAuth2 제공자로부터 받은 사용자 정보로 로그인/회원가입 처리
     *
     * @param email 소셜 계정 이메일
     * @param userName 소셜 계정 이름
     * @param signupType 가입 방법 (2: 구글, 3: 카카오, 4: 네이버)
     * @return 로그인 결과 Map
     */
    public Map<String, Object> socialLogin(String email, String userName, int signupType) {
        Map<String, Object> result = new HashMap<>();

        // 1. 이메일로 기존 사용자 조회
        Optional<UsersEntity> userOptional = usersRepository.findByEmail(email);

        UsersEntity user;

        if (userOptional.isEmpty()) {
            // 2-1. 신규 사용자: 자동 회원가입
            user = UsersEntity.builder()
                    .email(email)
                    .password("")  // 소셜 로그인은 비밀번호 없음
                    .user_name(userName)
                    .phone("")  // 추가 정보는 나중에 입력
                    .addr("")
                    .addr_detail("")
                    .signup_type(signupType)
                    .user_state(1)  // 활성 계정
                    .build();

            user = usersRepository.save(user);

            result.put("isNewUser", true);  // 신규 가입 여부

        } else {
            // 2-2. 기존 사용자: 로그인
            user = userOptional.get();

            // 계정 상태 확인
            if (user.getUser_state() != 1) {
                result.put("success", false);
                result.put("message", "비활성 계정입니다.");
                return result;
            }

            result.put("isNewUser", false);
        }

        // 3. JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(
                user.getUser_id(),
                user.getEmail(),
                user.getRole().name()
        );

        String refreshToken = jwtUtil.generateRefreshToken(
                user.getUser_id(),
                user.getEmail()
        );

        // 4. 로그인 성공 결과 반환
        result.put("success", true);
        result.put("message", "소셜 로그인 성공");
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        result.put("user", user.toDto());

        return result;
    }
}