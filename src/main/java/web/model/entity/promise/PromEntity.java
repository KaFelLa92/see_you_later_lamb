package web.model.entity.promise;

import jakarta.persistence.*;
import lombok.*;
import web.model.dto.promise.PromDto;
import web.model.entity.BaseTime;
import web.model.entity.user.UsersEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 약속(Promise) 엔티티
 * 사용자가 생성한 약속 정보를 관리하는 엔티티 클래스
 */
@Entity
@Table(name = "promise")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromEntity extends BaseTime {

    // ========== 1. 테이블 필드 설계 ==========

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private int promId;                    // 약속번호 (PK)

    @Column(nullable = false, length = 100) // NOT NULL, 최대 100자
    private String promTitle;              // 약속명

    /**
     * 약속 일시 필드
     * - null 가능: 시간 미정인 약속도 생성 가능
     */
    private LocalDateTime promDate;        // 약속일시

    /**
     * 약속 알림 시간 설정
     * - 약속 시간보다 이전에 알림을 하며, 분 단위로 측정
     * - 0이면 알람 없음
     * - 예: 30이면 약속 시간 30분 전에 알림
     */
    @Column(nullable = false)
    @Builder.Default
    private int promAlert = 0;             // 약속 알림 시간(분) (기본값: 0 = 알림 없음)

    private String promAddr;               // 약속 주소(장소)

    private String promAddrDetail;        // 약속 상세 장소

    /**
     * 약속 장소 위도
     * - 범위: -90 ~ 90
     * - CHECK 제약조건으로 유효성 검증
     */
    private Double promLat;                // 약속 장소 위도

    /**
     * 약속 장소 경도
     * - 범위: -180 ~ 180
     * - CHECK 제약조건으로 유효성 검증
     */
    private Double promLng;                // 약속 장소 경도

    @Column(nullable = false)
    private String promText;               // 약속 내용

    private String promMemo;               // 약속 메모/비고

    // ========== 2. 양방향 연결 ==========
    // 상위 엔티티가 하위 엔티티를 참조하는 관계

    /**
     * 양방향 연결: 약속 공유 목록
     * - 일대다(1:N) 관계
     * - 하나의 약속은 여러 명과 공유될 수 있음
     * - mappedBy: ShareEntity의 promEntity 필드와 연결
     */
    @OneToMany(mappedBy = "promEntity", fetch = FetchType.LAZY)
    @ToString.Exclude // 무한 참조 방지
    @Builder.Default
    private List<ShareEntity> shareEntityList = new ArrayList<>();

    /**
     * 양방향 연결: 반복 약속 목록
     * - 일대다(1:N) 관계
     * - 하나의 약속은 여러 반복 일정을 가질 수 있음
     * - mappedBy: CalendEntity의 promEntity 필드와 연결
     */
    @OneToMany(mappedBy = "promEntity", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<CalendEntity> calendEntityList = new ArrayList<>();

    // ========== 3. 단방향 연결 ==========
    // 하위 엔티티가 상위 엔티티를 참조하는 관계

    /**
     * 사용자(Users) 엔티티와의 다대일(N:1) 관계
     * - 여러 약속은 한 명의 사용자에 속함
     * - FetchType.LAZY: 지연 로딩 설정
     * - cascade 없음: 사용자 삭제 시 약속은 함께 삭제되지 않음
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 외래키(FK) 컬럼명 지정
    private UsersEntity usersEntity;

    // ========== 4. Entity -> Dto 변환 메서드 ==========
    /**
     * 엔티티를 DTO로 변환하는 메서드
     * @return PromDto 약속 데이터 전송 객체
     */
    public PromDto toDto() {
        return PromDto.builder()
                .promId(this.promId)
                .promTitle(this.promTitle)
                .promDate(this.promDate)
                .promAlert(this.promAlert)
                .promAddr(this.promAddr)
                .promAddrDetail(this.promAddrDetail)
                .promLat(this.promLat)
                .promLng(this.promLng)
                .promText(this.promText)
                .promMemo(this.promMemo)
                .userId(this.usersEntity.getUserId())
                .createDate(this.getCreateDate().toString())
                .updateDate(this.getUpdateDate().toString())
                .build();
    }

    // ========== 5. 비즈니스 로직 - 위도/경도 검증 ==========
    /**
     * 위도 유효성 검증 메서드
     * @param lat 위도 값
     * @return 유효하면 true, 아니면 false
     */
    public static boolean isValidLatitude(Double lat) {
        return lat == null || (lat >= -90 && lat <= 90);
    }

    /**
     * 경도 유효성 검증 메서드
     * @param lng 경도 값
     * @return 유효하면 true, 아니면 false
     */
    public static boolean isValidLongitude(Double lng) {
        return lng == null || (lng >= -180 && lng <= 180);
    }
}