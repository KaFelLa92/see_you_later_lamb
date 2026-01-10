package web.model.entity.lamb;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.lamb.ShepDto;
import web.model.entity.BaseTime;
import web.model.entity.user.UsersEntity;

/**
 * 양치기(Shepherd) 엔티티
 * 양과 사용자를 연결하는 엔티티 클래스
 * 사용자가 실제로 키우고 있는 양의 상태 정보를 관리
 */
@Entity
@Table(name = "shepherd")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShepEntity extends BaseTime {

    // ========== 1. 테이블 필드 설계 ==========

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private int shepId;                        // 양치기번호 (PK)

    /**
     * 양 별명 필드
     * - 사용자가 자신의 양에게 지어준 이름
     */
    @Column(nullable = false, length = 30) // NOT NULL, 최대 30자
    private String shepName;                   // 양 별명

    /**
     * 양 배고픔 상태 필드
     * -1: 배고픔
     * 0: 보통
     * 1: 배부름
     */
    @Column(nullable = false)
    @Builder.Default
    private int shepHunger = 1;                // 양 배고픔 상태 (기본값: 1 = 배부름)

    /**
     * 양털 상태 필드
     * -1: 털 많음 (깎을 필요 있음)
     * 0: 털 보통
     * 1: 털 없음 (최근에 깎음)
     */
    @Column(nullable = false)
    @Builder.Default
    private int shepFur = 1;                   // 양털 상태 (기본값: 1 = 털 없음)

    /**
     * 양 존재 여부 필드
     * 1: 울타리에 있음 (안전한 상태)
     * 0: 목장에 있음 (일반 상태)
     * -1: 늑대에 쫓기는 중 (사용자 소유가 아님)
     */
    @Column(nullable = false)
    @Builder.Default
    private int shepExist = 0;                 // 양 존재 여부 (기본값: 0 = 목장)

    // ========== 2. 양방향 연결 ==========
    // 상위 엔티티가 하위 엔티티를 참조하는 관계
    // (현재 없음)

    // ========== 3. 단방향 연결 ==========
    // 하위 엔티티가 상위 엔티티를 참조하는 관계

    /**
     * 양 정보(Lamb) 엔티티와의 다대일(N:1) 관계
     * - 여러 양치기는 같은 양 품종을 선택할 수 있음
     * - FetchType.LAZY: 지연 로딩 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lamb_id") // 외래키(FK) 컬럼명 지정
    private LambEntity lambEntity;

    /**
     * 사용자(Users) 엔티티와의 다대일(N:1) 관계
     * - 여러 양은 한 명의 사용자에 속함
     * - FetchType.LAZY: 지연 로딩 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 외래키(FK) 컬럼명 지정
    private UsersEntity usersEntity;

    // ========== 4. Entity -> Dto 변환 메서드 ==========
    /**
     * 엔티티를 DTO로 변환하는 메서드
     * @return ShepDto 양치기 데이터 전송 객체
     */
    public ShepDto toDto() {
        return ShepDto.builder()
                .shepId(this.shepId)
                .shepName(this.shepName)
                .shepHunger(this.shepHunger)
                .shepFur(this.shepFur)
                .shepExist(this.shepExist)
                .lambId(this.lambEntity.getLambId())
                .userId(this.usersEntity.getUserId())
                .createDate(this.getCreateDate().toString())
                .updateDate(this.getUpdateDate().toString())
                .build();
    }
}