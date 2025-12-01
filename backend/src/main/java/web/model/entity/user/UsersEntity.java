package web.model.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import web.model.dto.user.UsersDto;
import web.model.entity.BaseTime;
import web.model.entity.common.UserRole;
import web.model.entity.promise.PromEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 사용자(Users) 엔티티
 * 시스템의 사용자 정보를 관리하는 핵심 엔티티 클래스
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersEntity extends BaseTime {

    // ========== 1. 테이블 필드 설계 ==========

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private int userId;                                                    // 사용자번호 (PK)

    @Column(nullable = false, length = 40) // NOT NULL, 최대 40자
    private String email;                                                   // 이메일

    @Column(nullable = false, length = 40)
    private String password;                                                // 비밀번호

    @Column(nullable = false, length = 30)
    private String userName;                                               // 양치기 이름(닉네임)

    @Column(nullable = false, length = 15)
    private String phone;                                                   // 연락처

    @Column(nullable = false)
    private String addr;                                                    // 도로명 주소

    @Column(nullable = false)
    private String addrDetail;                                             // 상세 주소

    @Column(nullable = false)
    @Builder.Default
    private int point = 0;                                                  // 사용자 포인트 (기본값: 0)

    /**
     * 사용자 상태 필드
     * -1: 삭제 계정
     * 0: 휴면 계정
     * 1: 활동 계정
     */
    @Builder.Default
    private int userState = 1;                                             // 사용자 상태 (기본값: 1 = 활동중)

    /**
     * 가입 방법 필드
     * 1: 일반 회원가입
     * 2: 구글 소셜 로그인
     * 3: 카카오 소셜 로그인
     * 4: 네이버 소셜 로그인
     */
    @Builder.Default
    private int signupType = 1;                                            // 가입 방법 (기본값: 1 = 일반)

    /**
     * 사용자 권한 필드
     * - Enum 타입으로 권한 관리
     * - 기본값: ROLE_USER (일반 사용자)
     */
    @Enumerated(EnumType.STRING) // Enum을 문자열로 저장
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.ROLE_USER;                             // 사용자 권한

    // ========== 2. 양방향 연결 ==========
    // 상위 엔티티가 하위 엔티티를 참조하는 관계

    /**
     * 양방향 연결: 내가 요청한 친구 관계 목록
     * - 일대다(1:N) 관계
     * - mappedBy: FrenEntity의 offerUser 필드와 연결
     * - @ToString.Exclude: 무한 참조 방지
     */
    @OneToMany(mappedBy = "offerUser", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<FrenEntity> sentFriendRequests = new ArrayList<>();

    /**
     * 양방향 연결: 내가 받은 친구 관계 목록
     * - 일대다(1:N) 관계
     * - mappedBy: FrenEntity의 receiverUser 필드와 연결
     */
    @OneToMany(mappedBy = "receiverUser", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<FrenEntity> receivedFriendRequests = new ArrayList<>();

    /**
     * 양방향 연결: 출석 기록 목록
     * - 일대다(1:N) 관계
     * - mappedBy: AtenEntity의 usersEntity 필드와 연결
     */
    @OneToMany(mappedBy = "usersEntity", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<AtenEntity> atenEntityList = new ArrayList<>();

    /**
     * 양방향 연결: 설정 목록
     * - 일대다(1:N) 관계
     * - mappedBy: SetEntity의 usersEntity 필드와 연결
     */
    @OneToMany(mappedBy = "usersEntity", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<SetEntity> setEntityList = new ArrayList<>();

    /**
     * 양방향 연결: 약속 목록
     * - 일대다(1:N) 관계
     * - mappedBy: PromEntity의 usersEntity 필드와 연결
     */
    @OneToMany(mappedBy = "usersEntity", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<PromEntity> promEntityList = new ArrayList<>();

    // ========== 3. Entity -> Dto 변환 메서드 ==========
    /**
     * 엔티티를 DTO로 변환하는 메서드
     * @return UsersDto 사용자 데이터 전송 객체
     */
    public UsersDto toDto() {
        return UsersDto.builder()
                .userId(this.userId)
                .email(this.email)
                .password(this.password)
                .userName(this.userName)
                .phone(this.phone)
                .addr(this.addr)
                .addrDetail(this.addrDetail)
                .point(this.point)                                    // ✅ point 필드 추가
                .userState(this.userState)
                .signupType(this.signupType)
                .createDate(this.getCreateDate().toString())
                .updateDate(this.getUpdateDate().toString())
                .role(this.role)
                .build();
    }
}