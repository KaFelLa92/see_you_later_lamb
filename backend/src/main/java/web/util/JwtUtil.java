package web.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    // JWT 서명에 사용할 비밀키 (최소 256bit 이상 권장)
    // application.properties: jwt.secret=your-secret-key-minimum-256-bits-required-for-hs256-algorithm
    @Value("${jwt.secret}")
    private String secretKey;

    // Access Token 유효시간 (밀리초) - 기본 1시간
    @Value("${jwt.access-token-validity:3600000}")
    private long accessTokenValidity;

    // Refresh Token 유효시간 (밀리초) - 기본 7일
    @Value("${jwt.refresh-token-validity:604800000}")
    private long refreshTokenValidity;

    // ============================================
    // [2] JWT 토큰 생성 메서드
    // ============================================

    /**
     * Access Token 생성
     * @param userId 사용자 ID
     * @param email 사용자 이메일
     * @param role 사용자 권한
     * @return JWT Access Token 문자열
     */
    public String generateAccessToken(int userId, String email, String role) {
        // 토큰에 담을 사용자 정보 (Payload)
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);       // 사용자 ID
        claims.put("email", email);         // 이메일
        claims.put("role", role);           // 권한 (ROLE_USER, ROLE_ADMIN)
        claims.put("type", "access");       // 토큰 타입

        return createToken(claims, email, accessTokenValidity);
    }

    /**
     * Refresh Token 생성
     * @param userId 사용자 ID
     * @param email 사용자 이메일
     * @return JWT Refresh Token 문자열
     */
    public String generateRefreshToken(int userId, String email) {
        // Refresh Token은 최소한의 정보만 포함
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh");      // 토큰 타입

        return createToken(claims, email, refreshTokenValidity);
    }

    /**
     * JWT 토큰 생성 공통 로직
     * @param claims 토큰에 담을 정보 (Payload)
     * @param subject 토큰 제목 (일반적으로 사용자 식별자)
     * @param validity 유효 기간 (밀리초)
     * @return JWT 토큰 문자열
     */
    private String createToken(Map<String, Object> claims, String subject, long validity) {
        // 현재 시간
        Date now = new Date();
        // 만료 시간 = 현재 시간 + 유효기간
        Date expiryDate = new Date(now.getTime() + validity);

        // JWT 토큰 생성
        return Jwts.builder()
                .setClaims(claims)                      // Payload: 사용자 정보
                .setSubject(subject)                    // Subject: 토큰 제목 (이메일)
                .setIssuedAt(now)                       // 발행 시간
                .setExpiration(expiryDate)              // 만료 시간
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // 서명 알고리즘
                .compact();                             // 토큰 문자열로 변환
    }

    // ============================================
    // [3] JWT 토큰 검증 및 파싱 메서드
    // ============================================

    /**
     * JWT 토큰에서 사용자 ID 추출
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public Integer getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        // Integer로 안전하게 변환
        return claims.get("userId", Integer.class);
    }

    /**
     * JWT 토큰에서 이메일 추출
     * @param token JWT 토큰
     * @return 사용자 이메일
     */
    public String getEmailFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    /**
     * JWT 토큰에서 권한(Role) 추출
     * @param token JWT 토큰
     * @return 사용자 권한
     */
    public String getRoleFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    /**
     * JWT 토큰에서 토큰 타입 추출
     * @param token JWT 토큰
     * @return 토큰 타입 (access/refresh)
     */
    public String getTokenTypeFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("type", String.class);
    }

    /**
     * JWT 토큰에서 만료 시간 추출
     * @param token JWT 토큰
     * @return 만료 시간
     */
    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    /**
     * JWT 토큰에서 모든 Claim(정보) 추출
     * @param token JWT 토큰
     * @return Claims 객체
     */
    private Claims getAllClaimsFromToken(String token) {
        // JWT 파싱 및 서명 검증
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())         // 서명 검증용 키
                .build()
                .parseClaimsJws(token)                  // 토큰 파싱 + 서명 검증
                .getBody();                             // Payload(Claims) 반환
    }

    // ============================================
    // [4] JWT 토큰 유효성 검증 메서드
    // ============================================

    /**
     * JWT 토큰 유효성 검증 (종합)
     * @param token JWT 토큰
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            // 토큰 파싱 시도 (서명 검증 + 만료 확인)
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            return true;  // 예외 없이 성공하면 유효한 토큰

        } catch (SecurityException | MalformedJwtException e) {
            // 잘못된 서명 또는 형식
            System.out.println("Invalid JWT signature or format");
        } catch (ExpiredJwtException e) {
            // 만료된 토큰
            System.out.println("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            // 지원하지 않는 토큰
            System.out.println("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            // 빈 토큰
            System.out.println("JWT token is empty");
        }

        return false;  // 예외 발생 시 유효하지 않음
    }

    /**
     * Access Token 타입 검증
     * @param token JWT 토큰
     * @return Access Token이면 true
     */
    public boolean isAccessToken(String token) {
        try {
            String type = getTokenTypeFromToken(token);
            return "access".equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Refresh Token 타입 검증
     * @param token JWT 토큰
     * @return Refresh Token이면 true
     */
    public boolean isRefreshToken(String token) {
        try {
            String type = getTokenTypeFromToken(token);
            return "refresh".equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * JWT 토큰 만료 여부 확인
     * @param token JWT 토큰
     * @return 만료되었으면 true
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            // 만료 시간이 현재 시간보다 이전이면 만료
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            // 이미 만료된 토큰
            return true;
        } catch (Exception e) {
            // 기타 예외는 만료로 간주
            return true;
        }
    }

    // ============================================
    // [5] 헬퍼 메서드
    // ============================================

    /**
     * 서명에 사용할 Key 객체 생성
     * @return HMAC-SHA256 알고리즘용 Key
     */
    private Key getSigningKey() {
        // secretKey를 바이트 배열로 변환하여 Key 생성
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Bearer 토큰에서 실제 JWT 추출
     * HTTP 헤더: "Authorization: Bearer <token>"
     * @param bearerToken Bearer 토큰
     * @return JWT 토큰 (Bearer 제거)
     */
    public String extractTokenFromBearer(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // "Bearer " (7글자) 이후의 문자열 반환
            return bearerToken.substring(7);
        }
        return null;
    }
}