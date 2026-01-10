package web.model.entity.promise;

import jakarta.persistence.*;
import lombok.*;
import web.model.dto.promise.ShareDto;
import web.model.entity.BaseTime;
import web.model.entity.user.UsersEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 공유(Share) 엔티티
 * 약속 공유 정보와 평가 결과를 관리하는 엔티티 클래스
 */
@Entity
@Table(name = "share")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareEntity extends BaseTime {

    // ========== 1. 테이블 필드 설계 ==========

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private int shareId;                                   // 약속공유번호 (PK)

    /**
     * 공유 토큰 필드
     * - URL에 사용되는 고유한 토큰
     * - UNIQUE 제약조건으로 중복 방지
     * - @PrePersist 메서드로 자동 생성됨
     */
    @Column(nullable = false, unique = true, length = 50) // NOT NULL, UNIQUE
    private String shareToken;                             // 공유 토큰

    /**
     * 약속 확인 상태 필드
     * -1: 약속 어김
     * 0: 약속 이행
     * 1: 약속 잘 지킴
     */
    @Column(nullable = false)
    @Builder.Default
    private int shareCheck = 0;                            // 약속 확인 (기본값: 0)

    /**
     * 약속 점수 필드
     * - 1~5점 범위
     * - 5점일수록 좋은 평가
     * - 기본값: 3점 (보통)
     */
    @Column(nullable = false)
    @Builder.Default
    private int shareScore = 3;                            // 약속 점수 (기본값: 3점)

    /**
     * 약속 피드백 필드
     * - 평가자가 남기는 피드백 메시지
     * - 기본값: "약속 지켜줘서 고마워양!"
     */
    @Builder.Default
    private String shareFeedback = "약속 지켜줘서 고마워양!";  // 약속 피드백

    // ========== 2. 양방향 연결 ==========
    // 상위 엔티티가 하위 엔티티를 참조하는 관계

    /**
     * 양방향 연결: 약속 평가자 목록
     * - 일대다(1:N) 관계
     * - 하나의 공유된 약속은 여러 평가자를 가질 수 있음
     * - mappedBy: EvalEntity의 shareEntity 필드와 연결
     */
    @OneToMany(mappedBy = "shareEntity", fetch = FetchType.LAZY)
    @ToString.Exclude // 무한 참조 방지
    @Builder.Default
    private List<EvalEntity> evalEntityList = new ArrayList<>();

    // ========== 3. 단방향 연결 ==========
    // 하위 엔티티가 상위 엔티티를 참조하는 관계

    /**
     * 약속(Promise) 엔티티와의 다대일(N:1) 관계
     * - 여러 공유는 하나의 약속에 속함
     * - FetchType.LAZY: 지연 로딩 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prom_id") // 외래키(FK) 컬럼명 지정
    private PromEntity promEntity;

    // ========== 4. 엔티티 생성 시 토큰 자동 생성 ==========
    /**
     * 엔티티가 DB에 저장되기 전에 자동으로 실행되는 메서드
     * - 공유 토큰이 없으면 UUID를 사용하여 고유한 토큰 생성
     * - UUID의 하이픈(-) 제거 후 앞 20자만 사용
     */
    @PrePersist
    public void generateToken() {
        if (this.shareToken == null || this.shareToken.isEmpty()) {
            // UUID를 사용해 고유한 토큰 생성
            this.shareToken = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        }
    }

    // ========== 5. Entity -> Dto 변환 메서드 ==========
    /**
     * 엔티티를 DTO로 변환하는 메서드
     * @return ShareDto 공유 데이터 전송 객체
     */
    public ShareDto toDto() {
        return ShareDto.builder()
                .shareId(this.shareId)
                .shareToken(this.shareToken)
                .shareCheck(this.shareCheck)
                .shareScore(this.shareScore)
                .shareFeedback(this.shareFeedback)
                .promId(this.promEntity.getPromId())
                .createDate(this.getCreateDate().toString())
                .updateDate(this.getUpdateDate().toString())
                .build();
    }
}