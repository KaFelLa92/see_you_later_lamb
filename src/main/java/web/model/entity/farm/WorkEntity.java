package web.model.entity.farm;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.farm.WorkDto;
import web.model.entity.BaseTime;

/**
 * 목장 업무(Farm Work) 엔티티
 * 목장에서 수행할 수 있는 업무 정보를 관리하는 엔티티 클래스
 */
@Entity
@Table(name = "farmWork")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkEntity extends BaseTime {

    // ========== 1. 테이블 필드 설계 ==========

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private int workId;                    // 목장업무번호 (PK)

    /**
     * 목장 업무명 필드
     * - 업무의 이름
     * - 예: "양 먹이주기", "양털 깎기", "울타리 수리" 등
     */
    @Column(nullable = false, length = 50) // NOT NULL, 최대 50자
    private String workName;               // 목장 업무명

    /**
     * 업무 상태 필드
     * -1: 기한 종료 (실패)
     * 0: 미실행 (대기 중)
     * 1: 완료 (성공)
     */
    @Column(nullable = false)
    @Builder.Default
    private int workState = 0;             // 업무 상태 (기본값: 0 = 미실행)

    /**
     * 업무 안내 종료 일시 필드
     * - 업무를 완료했거나 기한이 종료된 시간
     */
    @Column(nullable = false)
    private LocalDateTime workEndDate;    // 업무 안내 종료 일시

    // ========== 2. 양방향 연결 ==========
    // 상위 엔티티가 하위 엔티티를 참조하는 관계
    // (현재 없음)

    // ========== 3. 단방향 연결 ==========
    // 하위 엔티티가 상위 엔티티를 참조하는 관계

    /**
     * 목장주(Owner) 엔티티와의 다대일(N:1) 관계
     * - 여러 업무는 한 명의 목장주에 속함
     * - FetchType.LAZY: 지연 로딩 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id") // 외래키(FK) 컬럼명 지정
    private OwnerEntity ownerEntity;

    // ========== 4. Entity -> Dto 변환 메서드 ==========
    /**
     * 엔티티를 DTO로 변환하는 메서드
     * @return WorkDto 목장 업무 데이터 전송 객체
     */
    public WorkDto toDto() {
        return WorkDto.builder()
                .workId(this.workId)
                .workName(this.workName)
                .workState(this.workState)
                .workEndDate(this.workEndDate)
                .ownerId(this.ownerEntity.getOwnerId())
                .createDate(this.getCreateDate().toString())
                .updateDate(this.getUpdateDate().toString())
                .build();
    }
}