package web.model.entity.lamb;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.lamb.LambCharDto;
import web.model.entity.BaseTime;

/**
 * 양 특성(Lamb Characteristic) 엔티티
 * 양의 특성과 능력치 정보를 관리하는 엔티티 클래스
 */
@Entity
@Table(name = "lambChar")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambCharEntity extends BaseTime {

    // ========== 1. 테이블 필드 설계 ==========

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private int charId;                // 양특성번호 (PK)

    /**
     * 특성명 필드
     * - 양의 특성 이름
     * - 예: "빠른 이동", "높은 점프", "강한 방어" 등
     */
    @Column(nullable = false, length = 30) // NOT NULL, 최대 30자
    private String charName;           // 특성명

    /**
     * 특성 설명 필드
     * - 해당 특성에 대한 상세 설명
     */
    @Column(nullable = false)
    private String charDesc;           // 특성 설명

    /**
     * 효과 분류 필드
     * - 특성의 효과 타입을 정의
     * - int 또는 json 형태로 저장
     * - 파싱하여 사용 필요
     */
    @Column(nullable = false, length = 30)
    private String effectType;         // 효과 분류 (int/json 파싱 필요)

    /**
     * 효과 값 필드
     * - 특성의 구체적인 효과 수치
     */
    @Column(nullable = false)
    private String effectValue;        // 효과 값

    /**
     * 활성화 여부 필드
     * - 1: 활성화됨
     * - 0: 비활성화됨
     */
    private int isActive;              // 활성화 여부 (1 or 0)

    // ========== 2. 양방향 연결 ==========
    // 상위 엔티티가 하위 엔티티를 참조하는 관계
    // (현재 없음)

    // ========== 3. 단방향 연결 ==========
    // 하위 엔티티가 상위 엔티티를 참조하는 관계
    // (현재 없음)

    // ========== 4. Entity -> Dto 변환 메서드 ==========
    /**
     * 엔티티를 DTO로 변환하는 메서드
     * @return LambCharDto 양 특성 데이터 전송 객체
     */
    public LambCharDto toDto() {
        return LambCharDto.builder()
                .charId(this.charId)
                .charName(this.charName)
                .charDesc(this.charDesc)
                .effectType(this.effectType)
                .effectValue(this.effectValue)
                .isActive(this.isActive)
                .createDate(this.getCreateDate().toString())
                .updateDate(this.getUpdateDate().toString())
                .build();
    }
}