package web.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.common.UserRole;
import web.model.entity.user.UsersEntity;

/**
 * 사용자(Users) DTO
 * 회원가입, 로그인 등 사용자 정보를 전달하기 위한 데이터 전송 객체
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersDto {

    // ========== 1. 필드 설계 ==========

    private int userId;        // 사용자번호 (PK)
    private String email;       // 이메일
    private String password;    // 비밀번호
    private String userName;   // 양치기 이름(닉네임)
    private String phone;       // 연락처
    private String addr;        // 도로명 주소
    private String addrDetail; // 상세 주소

    /**
     * 사용자 상태
     * -1: 삭제 계정
     * 0: 휴면 계정
     * 1: 활동 계정
     */
    private int userState;     // 사용자 상태

    /**
     * 가입 방법
     * 1: 일반
     * 2: 구글
     * 3: 카카오
     * 4: 네이버
     */
    private int signupType;    // 가입 방법

    private String createDate; // 생성일
    private String updateDate; // 수정일
    private UserRole role;      // 권한

    // ========== 2. Dto -> Entity 변환 메서드 ==========
    /**
     * DTO를 엔티티로 변환하는 메서드 (생성용)
     * @return UsersEntity 사용자 엔티티
     */
    public UsersEntity toEntity() {
        return UsersEntity.builder()
                .userId(userId)
                .email(email)
                .password(password)
                .userName(userName)
                .phone(phone)
                .addr(addr)
                .addrDetail(addrDetail)
                .userState(userState)
                .signupType(signupType)
                .role(role)
                .build();
    }
}