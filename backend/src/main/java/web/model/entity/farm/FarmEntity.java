package web.model.entity.farm;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.farm.FarmDto;
import web.model.entity.BaseTime;
import web.model.entity.user.UsersEntity;

/**
 * 목장 정보(Farm Information) 엔티티
 * 목장의 기본 정보와 사양을 관리하는 엔티티 클래스
 */
@Entity
@Table(name = "farmInfo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmEntity extends BaseTime {
 
    // ========== 1. 테이블 필드 설계 ==========

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private int farmId;                    // 목장번호 (PK)

    /**
     * 목장명 필드
     * - 목장의 이름
     */
    @Column(nullable = false, length = 50) // NOT NULL, 최대 50자
    private String farmName;               // 목장명

    /**
     * 목장 소개 필드
     * - 목장에 대한 설명
     */
    @Column(nullable = false)
    private String farmInfo;               // 목장 소개

    /**
     * 최대 양 숫자 필드
     * - 목장에서 기를 수 있는 최대 양의 수
     * - 기본값: 10마리
     */
    @Column(nullable = false)
    @Builder.Default
    private int maxLamb = 10;              // 최대 양 숫자 (기본값: 10)

    /**
     * 목장 구매 비용 필드
     * - 목장을 구매하는데 필요한 포인트
     */
    @Column(nullable = false)
    private int farmCost;                  // 목장 구매 비용

    // ========== 2. 양방향 연결 ==========
    // 상위 엔티티가 하위 엔티티를 참조하는 관계
    // (현재 없음)

    // ========== 3. 단방향 연결 ==========
    // 하위 엔티티가 상위 엔티티를 참조하는 관계

    /**
     * 사용자(Users) 엔티티와의 다대일(N:1) 관계
     * - 여러 목장은 한 명의 사용자에 속함
     * - FetchType.LAZY: 지연 로딩 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 외래키(FK) 컬럼명 지정
    private UsersEntity usersEntity;

    // ========== 4. Entity -> Dto 변환 메서드 ==========
    /**
     * 엔티티를 DTO로 변환하는 메서드
     * @return FarmDto 목장 정보 데이터 전송 객체
     */
    public FarmDto toDto() {
        return FarmDto.builder()
                .farmId(this.farmId)
                .farmName(this.farmName)
                .farmInfo(this.farmInfo)
                .maxLamb(this.maxLamb)
                .farmCost(this.farmCost)
                .userId(this.usersEntity.getUserId())
                .createDate(this.getCreateDate().toString())
                .updateDate(this.getUpdateDate().toString())
                .build();
    }
}