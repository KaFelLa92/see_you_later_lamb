package web.model.entity.lamb;

import jakarta.persistence.*;
import lombok.*;
import web.model.dto.lamb.LambDto;
import web.model.entity.BaseTime;
import web.model.entity.common.LambRank;

import java.util.ArrayList;
import java.util.List;

/**
 * 양 정보(Lamb Information) 엔티티
 * 양의 기본 정보와 등급을 관리하는 엔티티 클래스
 */
@Entity
@Table(name = "lambInfo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambEntity extends BaseTime {

    // ========== 1. 테이블 필드 설계 ==========

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private int lambId;                            // 양번호 (PK)

    /**
     * 양 품종 필드
     * - 예: 풍성해양, 겁없어양, 배고파양, 전기양, 콜리닮았어양
     */
    @Column(nullable = false, length = 30) // NOT NULL, 최대 30자
    private String lambName;                       // 양 품종

    /**
     * 양 소개 필드
     * - 해당 양 품종에 대한 설명
     */
    @Column(nullable = false)
    private String lambInfo;                       // 양 소개

    /**
     * 양 등급 필드
     * - Enum 타입으로 등급 관리
     * - 1: 일반(COMMON)
     * - 2: 희귀(RARE)
     * - 3: 특급(EPIC)
     * - 4: 전설(LEGENDARY)
     */
    @Column(nullable = false)
    @Builder.Default
    private LambRank lambRank = LambRank.COMMON;   // 양 등급 (기본값: COMMON)

    /**
     * 양 일러스트 경로 필드
     * - 양의 이미지 파일 경로
     */
    private String lambPath;                       // 양 일러스트 경로

    // ========== 2. 양방향 연결 ==========
    // 상위 엔티티가 하위 엔티티를 참조하는 관계

    /**
     * 양방향 연결: 양치기(Shepherd) 목록
     * - 일대다(1:N) 관계
     * - 하나의 양 품종은 여러 양치기(사용자가 키우는 양)를 가질 수 있음
     * - mappedBy: ShepEntity의 lambEntity 필드와 연결
     */
    @OneToMany(mappedBy = "lambEntity", fetch = FetchType.LAZY)
    @ToString.Exclude // 무한 참조 방지
    @Builder.Default
    private List<ShepEntity> shepEntityList = new ArrayList<>();

    // ========== 3. 단방향 연결 ==========
    // 하위 엔티티가 상위 엔티티를 참조하는 관계

    /**
     * 양 특성(Lamb Characteristic) 엔티티와의 다대일(N:1) 관계
     * - 여러 양은 하나의 특성을 가질 수 있음
     * - FetchType.LAZY: 지연 로딩 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "char_id") // 외래키(FK) 컬럼명 지정
    private LambCharEntity lambCharEntity;

    // ========== 4. Entity -> Dto 변환 메서드 ==========
    /**
     * 엔티티를 DTO로 변환하는 메서드
     * @return LambDto 양 정보 데이터 전송 객체
     */
    public LambDto toDto() {
        return LambDto.builder()
                .lambId(this.lambId)
                .lambName(this.lambName)
                .lambInfo(this.lambInfo)
                .lambRank(this.lambRank)
                .lambPath(this.lambPath)
                .charId(this.lambCharEntity.getCharId())
                .createDate(this.getCreateDate().toString())
                .updateDate(this.getUpdateDate().toString())
                .build();
    }
}