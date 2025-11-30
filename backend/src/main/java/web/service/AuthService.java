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
 * - Access Token과 Refresh Token을 사용한 JWT 인증 구현
 */
@Service
@RequiredArgsConstructor  // final 필드에 대한 생성자 자동 생성 (의존성 주입용)
@Transactional            // 클래스 레벨의 트랜잭션 처리 (모든 메서드에 적용)
public class AuthService {

    // ============================================
    // [*] DI (Dependency Injection) - 의존성 주입
    // ============================================

    /**
     * 사용자 Repository
     * 데이터베이스에서 사용자 정보를 조회/저장하는 역할
     */
    private final UsersRepository usersRepository;

    /**
     * JWT 유틸리티 클래스
     * JWT 토큰 생성, 검증, 파싱 등의 기능 제공
     */
    private final JwtUtil jwtUtil;

    // ============================================
    // [1] 로그인 (JWT 토큰 발급)
    // ============================================

    /**
     * 로그인 처리 및 JWT 토큰 발급
     * 이메일과 비밀번호를 검증하고 성공 시 Access Token과 Refresh Token 발급
     *
     * @param email 이메일
     * @param password 비밀번호
     * @return Map<String, Object> 로그인 결과
     *         - success: 성공 여부 (boolean)
     *         - message: 결과 메시지 (String)
     *         - accessToken: Access Token (String, 성공 시)
     *         - refreshToken: Refresh Token (String, 성공 시)
     *         - user: 사용자 정보 (UsersDto, 성공 시)
     */
    public Map<String, Object> login(String email, String password) {
        // 결과를 담을 Map 생성
        Map<String, Object> result = new HashMap<>();

        // 1. 이메일로 사용자 조회
        // Optional: 값이 있을 수도, 없을 수도 있는 컨테이너
        Optional<UsersEntity> userOptional = usersRepository.findByEmail(email);

        // 2. 사용자가 존재하지 않는 경우
        if (userOptional.isEmpty()) {
            result.put("success", false);
            result.put("message", "이메일 또는 비밀번호가 일치하지 않습니다.");
            return result;
        }

        // 3. 사용자 정보 가져오기
        UsersEntity user = userOptional.get();

        // 4. 비밀번호 검증
        // 실제로는 BCrypt 등으로 암호화된 비밀번호 비교 필요
        // TODO: BCryptPasswordEncoder.matches(password, user.getPassword()) 사용
        if (!user.getPassword().equals(password)) {
            result.put("success", false);
            result.put("message", "이메일 또는 비밀번호가 일치하지 않습니다.");
            return result;
        }

        // 5. 계정 상태 확인
        // userState: -1(탈퇴), 0(휴면), 1(정상)
        if (user.getUserState() == -1) {
            result.put("success", false);
            result.put("message", "탈퇴한 계정입니다.");
            return result;
        }

        if (user.getUserState() == 0) {
            result.put("success", false);
            result.put("message", "휴면 계정입니다. 계정 활성화가 필요합니다.");
            return result;
        }

        // 6. JWT 토큰 생성
        // Access Token: 실제 API 호출에 사용 (짧은 만료 시간)
        String accessToken = jwtUtil.generateAccessToken(
                user.getUserId(),            // 사용자 ID
                user.getEmail(),             // 이메일
                user.getRole().name()        // 권한 (ROLE_USER, ROLE_ADMIN 등)
        );

        // Refresh Token: Access Token 재발급에 사용 (긴 만료 시간)
        String refreshToken = jwtUtil.generateRefreshToken(
                user.getUserId(),
                user.getEmail()
        );

        // 7. 로그인 성공 결과 반환
        result.put("success", true);
        result.put("message", "로그인 성공");
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        result.put("user", user.toDto());  // Entity를 DTO로 변환하여 반환

        return result;
    }

    // ============================================
    // [2] 토큰 재발급 (Refresh Token으로 Access Token 갱신)
    // ============================================

    /**
     * Refresh Token으로 새로운 Access Token 발급
     * Access Token이 만료되었을 때 Refresh Token을 사용하여 새로 발급받음
     *
     * @param refreshToken Refresh Token
     * @return Map<String, Object> 재발급 결과
     *         - success: 성공 여부
     *         - message: 결과 메시지
     *         - accessToken: 새로운 Access Token (성공 시)
     */
    public Map<String, Object> refreshAccessToken(String refreshToken) {
        Map<String, Object> result = new HashMap<>();

        // 1. Refresh Token 유효성 검증
        // 토큰이 만료되었거나 위조된 경우 false 반환
        if (!jwtUtil.validateToken(refreshToken)) {
            result.put("success", false);
            result.put("message", "유효하지 않거나 만료된 Refresh Token입니다.");
            return result;
        }

        // 2. Refresh Token 타입 확인
        // Access Token으로 재발급을 시도하는 것을 방지
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            result.put("success", false);
            result.put("message", "Refresh Token이 아닙니다.");
            return result;
        }

        // 3. Token에서 사용자 정보 추출
        Integer userId = jwtUtil.getUserIdFromToken(refreshToken);
        String email = jwtUtil.getEmailFromToken(refreshToken);

        // 4. 사용자 조회 (DB에서 최신 정보 확인)
        // Token은 유효하지만 사용자가 탈퇴했을 수 있으므로 DB 재확인 필요
        Optional<UsersEntity> userOptional = usersRepository.findById(userId);

        if (userOptional.isEmpty()) {
            result.put("success", false);
            result.put("message", "존재하지 않는 사용자입니다.");
            return result;
        }

        UsersEntity user = userOptional.get();

        // 5. 계정 상태 확인
        // 휴면이나 탈퇴 상태인 경우 토큰 재발급 불가
        if (user.getUserState() != 1) {
            result.put("success", false);
            result.put("message", "비활성 계정입니다.");
            return result;
        }

        // 6. 새로운 Access Token 생성
        String newAccessToken = jwtUtil.generateAccessToken(
                user.getUserId(),
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
     * API 요청 시 토큰의 유효성을 검증하고 사용자 정보를 추출
     *
     * @param accessToken Access Token
     * @return Map<String, Object> 검증 결과
     *         - valid: 유효성 여부
     *         - message: 결과 메시지 (실패 시)
     *         - userId: 사용자 ID (성공 시)
     *         - email: 이메일 (성공 시)
     *         - role: 권한 (성공 시)
     */
    public Map<String, Object> validateAccessToken(String accessToken) {
        Map<String, Object> result = new HashMap<>();

        // 1. 토큰 유효성 검증
        // 만료, 위조, 형식 오류 등을 확인
        if (!jwtUtil.validateToken(accessToken)) {
            result.put("valid", false);
            result.put("message", "유효하지 않거나 만료된 토큰입니다.");
            return result;
        }

        // 2. Access Token 타입 확인
        // Refresh Token으로 API 호출을 시도하는 것을 방지
        if (!jwtUtil.isAccessToken(accessToken)) {
            result.put("valid", false);
            result.put("message", "Access Token이 아닙니다.");
            return result;
        }

        // 3. 토큰에서 사용자 정보 추출
        // JWT의 페이로드에 저장된 정보를 파싱
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
     * Access Token으로 사용자의 상세 정보를 DB에서 조회
     *
     * @param accessToken Access Token
     * @return UsersDto 사용자 정보 DTO (실패 시 null)
     */
    public UsersDto getUserFromToken(String accessToken) {
        // 1. 토큰 검증 (유효성 + 타입 확인)
        if (!jwtUtil.validateToken(accessToken) || !jwtUtil.isAccessToken(accessToken)) {
            return null;
        }

        // 2. 사용자 ID 추출
        Integer userId = jwtUtil.getUserIdFromToken(accessToken);

        // 3. DB에서 사용자 조회
        Optional<UsersEntity> userOptional = usersRepository.findById(userId);

        // 4. Entity를 DTO로 변환하여 반환
        // map: Optional 안의 값이 있으면 변환, 없으면 orElse 실행
        return userOptional.map(UsersEntity::toDto).orElse(null);
    }

    // ============================================
    // [4] 로그아웃 (토큰 무효화)
    // ============================================

    /**
     * 로그아웃
     * JWT는 stateless이므로 서버에서 토큰을 삭제할 수 없음
     *
     * 구현 방법:
     * 1. 클라이언트에서 토큰 삭제 (LocalStorage, AsyncStorage 등)
     * 2. (옵션) Redis 등에 블랙리스트로 등록하여 해당 토큰 사용 차단
     *
     * @param accessToken Access Token
     * @return Map<String, Object> 로그아웃 결과
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
        //
        // 예시 코드:
        // long expirationTime = jwtUtil.getExpirationTime(accessToken);
        // redisTemplate.opsForValue().set(
        //     "blacklist:" + accessToken,
        //     "logout",
        //     expirationTime,
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
     * 동작 방식:
     * - 기존 회원: 로그인 처리
     * - 신규 회원: 자동 회원가입 후 로그인
     *
     * @param email 소셜 계정 이메일
     * @param userName 소셜 계정 이름
     * @param signupType 가입 방법 (2: 구글, 3: 카카오, 4: 네이버)
     * @return Map<String, Object> 로그인 결과
     *         - success: 성공 여부
     *         - message: 결과 메시지
     *         - accessToken: Access Token
     *         - refreshToken: Refresh Token
     *         - user: 사용자 정보
     *         - isNewUser: 신규 가입 여부
     */
    public Map<String, Object> socialLogin(String email, String userName, int signupType) {
        Map<String, Object> result = new HashMap<>();

        // 1. 이메일로 기존 사용자 조회
        Optional<UsersEntity> userOptional = usersRepository.findByEmail(email);

        UsersEntity user;

        if (userOptional.isEmpty()) {
            // 2-1. 신규 사용자: 자동 회원가입

            // Builder 패턴으로 Entity 생성
            user = UsersEntity.builder()
                    .email(email)
                    .password("")          // 소셜 로그인은 비밀번호 없음
                    .userName(userName)
                    .phone("")             // 추가 정보는 나중에 입력
                    .addr("")
                    .addrDetail("")
                    .signupType(signupType)
                    .userState(1)          // 활성 계정으로 시작
                    .build();

            // DB에 저장
            user = usersRepository.save(user);

            result.put("isNewUser", true);  // 신규 가입 여부 표시

        } else {
            // 2-2. 기존 사용자: 로그인
            user = userOptional.get();

            // 계정 상태 확인
            if (user.getUserState() != 1) {
                result.put("success", false);
                result.put("message", "비활성 계정입니다.");
                return result;
            }

            result.put("isNewUser", false);
        }

        // 3. JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(
                user.getUserId(),
                user.getEmail(),
                user.getRole().name()
        );

        String refreshToken = jwtUtil.generateRefreshToken(
                user.getUserId(),
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