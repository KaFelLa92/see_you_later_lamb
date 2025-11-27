package web.model.entity.promise;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.promise.CalendDto;
import web.model.entity.BaseTime;
import web.model.entity.common.CycleType;

import java.time.LocalDateTime;

/**
 * 캘린더(Calendar) 엔티티
 * 반복되는 약속의 일정 정보를 관리하는 엔티티 클래스
 */
@Entity
@Table(name = "calender")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendEntity extends BaseTime {

    // ========== 1. 테이블 필드 설계 ==========

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private int calendId;              // 캘린더번호 (PK)

    /**
     * 반복 주기 필드
     * - Enum 타입으로 반복 주기 관리
     * - 예: 매일, 매주, 매월, 매년 등
     */
    @Enumerated(EnumType.STRING) // Enum을 문자열로 저장
    private CycleType calendCycle;     // 반복 주기

    @Column(nullable = false) // NOT NULL 제약조건
    private LocalDateTime calendStart; // 반복 시작일

    @Column(nullable = false)
    private LocalDateTime calendEnd;   // 반복 종료일

    // ========== 2. 양방향 연결 ==========
    // 상위 엔티티가 하위 엔티티를 참조하는 관계
    // (현재 없음)

    // ========== 3. 단방향 연결 ==========
    // 하위 엔티티가 상위 엔티티를 참조하는 관계

    /**
     * 약속(Promise) 엔티티와의 다대일(N:1) 관계
     * - 여러 반복 일정은 하나의 약속에 속함
     * - FetchType.LAZY: 지연 로딩 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prom_id") // 외래키(FK) 컬럼명 지정
    private PromEntity promEntity;

    // ========== 4. Entity -> Dto 변환 메서드 ==========
    /**
     * 엔티티를 DTO로 변환하는 메서드
     * @return CalendDto 캘린더 데이터 전송 객체
     */
    public CalendDto toDto() {
        return CalendDto.builder()
                .calendId(this.calendId)
                .calendCycle(this.calendCycle)
                .calendStart(this.calendStart)
                .calendEnd(this.calendEnd)
                .promId(this.promEntity.getPromId())
                .createDate(this.getCreateDate().toString())
                .updateDate(this.getUpdateDate().toString())
                .build();
    }
}