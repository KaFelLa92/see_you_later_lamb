package web.model.entity.lamb;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.lamb.ProbDto;
import web.model.entity.BaseTime;
import web.model.entity.promise.ShareEntity;

/**
 * 등장 확률(Probability) 엔티티
 * 양과 늑대의 등장 확률 정보를 관리하는 엔티티 클래스
 * 약속 공유 평가에 따라 확률이 조정됨
 */
@Entity
@Table(name = "probability")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProbEntity extends BaseTime {

    // ========== 1. 테이블 필드 설계 ==========

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private int probId;                    // 등장확률번호 (PK)

    /**
     * 양 등장 확률 필드
     * - 양이 등장할 확률(퍼센트)
     */
    @Column(nullable = false) // NOT NULL 제약조건
    private int probLamb;                  // 양 등장 확률

    /**
     * 늑대 등장 확률 필드
     * - 늑대가 등장할 확률(퍼센트)
     */
    @Column(nullable = false)
    private int probWolf;                  // 늑대 등장 확률

    /**
     * 희귀 등급 등장 확률 필드
     * - 일반 양을 제외한 희귀, 특급, 전설 등급의 등장 확률
     * - 해당 레코드만큼 확률 보정이 들어감
     */
    @Column(nullable = false)
    private int probRare;                  // 희귀 등급 등장 확률

    // ========== 2. 양방향 연결 ==========
    // 상위 엔티티가 하위 엔티티를 참조하는 관계
    // (현재 없음)

    // ========== 3. 단방향 연결 ==========
    // 하위 엔티티가 상위 엔티티를 참조하는 관계

    /**
     * 약속공유(Share) 엔티티와의 다대일(N:1) 관계
     * - 약속 평가 결과에 따라 등장 확률이 달라짐
     * - FetchType.LAZY: 지연 로딩 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_id") // 외래키(FK) 컬럼명 지정
    private ShareEntity shareEntity;

    // ========== 4. Entity -> Dto 변환 메서드 ==========
    /**
     * 엔티티를 DTO로 변환하는 메서드
     * @return ProbDto 등장 확률 데이터 전송 객체
     */
    public ProbDto toDto() {
        return ProbDto.builder()
                .probId(this.probId)
                .probLamb(this.probLamb)
                .probWolf(this.probWolf)
                .probRare(this.probRare)
                .shareId(this.shareEntity.getShareId())
                .createDate(this.getCreateDate().toString())
                .updateDate(this.getUpdateDate().toString())
                .build();
    }
}