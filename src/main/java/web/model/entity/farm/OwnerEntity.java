package web.model.entity.farm;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.farm.OwnerDto;
import web.model.entity.BaseTime;
import web.model.entity.user.UsersEntity;

/**
 * 목장주(Farm Owner) 엔티티
 * 목장과 사용자를 연결하는 엔티티 클래스
 * 사용자가 소유한 목장 정보를 관리
 */
@Entity
@Table(name = "farmOwner")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OwnerEntity extends BaseTime {

    // ========== 1. 테이블 필드 설계 ==========

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private int ownerId;                   // 목장주번호 (PK)

    /**
     * 목장 별명 필드
     * - 사용자가 자신의 목장에 지어준 이름
     */
    @Column(nullable = false, length = 50) // NOT NULL, 최대 50자
    private String ownerName;              // 목장 별명

    // ========== 2. 양방향 연결 ==========
    // 상위 엔티티가 하위 엔티티를 참조하는 관계
    // (현재 없음)

    // ========== 3. 단방향 연결 ==========
    // 하위 엔티티가 상위 엔티티를 참조하는 관계

    /**
     * 목장(Farm) 엔티티와의 다대일(N:1) 관계
     * - 여러 목장주는 같은 목장 유형을 가질 수 있음
     * - FetchType.LAZY: 지연 로딩 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id") // 외래키(FK) 컬럼명 지정
    private FarmEntity farmEntity;

    /**
     * 사용자(Users) 엔티티와의 다대일(N:1) 관계
     * - 여러 목장주는 한 명의 사용자에 속함
     * - FetchType.LAZY: 지연 로딩 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 외래키(FK) 컬럼명 지정
    private UsersEntity usersEntity;

    // ========== 4. Entity -> Dto 변환 메서드 ==========
    /**
     * 엔티티를 DTO로 변환하는 메서드
     * @return OwnerDto 목장주 데이터 전송 객체
     */
    public OwnerDto toDto() {
        return OwnerDto.builder()
                .ownerId(this.ownerId)
                .ownerName(this.ownerName)
                .farmId(this.farmEntity.getFarmId())
                .userId(this.usersEntity.getUserId())
                .createDate(this.getCreateDate().toString())
                .updateDate(this.getUpdateDate().toString())
                .build();
    }
}