package web.model.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.user.SetDto;
import web.model.entity.BaseTime;
import web.model.entity.common.LangType;
import web.model.entity.common.TrafficType;

/**
 * 설정(Setting) 엔티티
 * 사용자의 개인 설정 정보를 관리하는 엔티티 클래스
 */
@Entity
@Table(name = "setting")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetEntity extends BaseTime {

    // ========== 1. 테이블 필드 설계 ==========

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private int setId;         // 설정번호 (PK)

    /**
     * 약속 리마인드 시간 설정
     * - 약속 시간 기준으로 '몇 분 전'에 알람을 줄 것인가
     * - 0일 경우 리마인드 설정 해제
     * - 단위: 분(minute)
     */
    @Column(nullable = false) // NOT NULL 제약조건
    @Builder.Default
    private int setRemind = 0; // 약속 리마인드 (기본값: 0 = 알림 없음)

    /**
     * 업무 표시 설정
     * - 시간마다 들어오는 목장 업무를 플레이할 것인가의 여부
     * - 0: 플레이 안 함
     * - 1: 플레이 함
     */
    @Column(nullable = false)
    @Builder.Default
    private int setWork = 1;   // 업무 표시 (기본값: 1 = 플레이함)

    /**
     * 우선 교통수단 설정
     * - Enum 타입으로 교통수단 선택
     * - 기본값: 지하철 + 버스
     */
    @Enumerated(EnumType.STRING) // Enum을 문자열로 저장
    @Column(nullable = false)
    @Builder.Default
    private TrafficType setTraffic = TrafficType.SUBWAY_AND_BUS;   // 우선 교통수단

    /**
     * 언어 설정
     * - Enum 타입으로 언어 선택
     * - 기본값: 한국어
     */
    @Enumerated(EnumType.STRING) // Enum을 문자열로 저장
    @Column(nullable = false)
    @Builder.Default
    private LangType setLanguage = LangType.KOREAN;                    // 언어 설정

    // ========== 2. 양방향 연결 ==========
    // 상위 엔티티가 하위 엔티티를 참조하는 관계
    // (현재 없음)

    // ========== 3. 단방향 연결 ==========
    // 하위 엔티티가 상위 엔티티를 참조하는 관계

    /**
     * 사용자(Users) 엔티티와의 다대일(N:1) 관계
     * - 한 명의 사용자는 여러 설정 기록을 가질 수 있음
     * - FetchType.LAZY: 지연 로딩 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 외래키(FK) 컬럼명 지정
    private UsersEntity usersEntity;

    // ========== 4. Entity -> Dto 변환 메서드 ==========
    /**
     * 엔티티를 DTO로 변환하는 메서드
     * @return SetDto 설정 데이터 전송 객체
     */
    public SetDto toDto() {
        return SetDto.builder()
                .setId(this.setId)
                .setRemind(this.setRemind)
                .setWork(this.setWork)
                .setTraffic(this.setTraffic)
                .setLanguage(this.setLanguage)
                .userId(this.usersEntity.getUserId())
                .createDate(this.getCreateDate().toString())
                .updateDate(this.getUpdateDate().toString())
                .build();
    }
}