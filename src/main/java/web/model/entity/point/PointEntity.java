package web.model.entity.point;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.point.PointDto;
import web.model.entity.BaseTime;

/**
 * 포인트 정책(Point Policy) 엔티티
 * 포인트 적립 공식과 규칙을 관리하는 엔티티 클래스
 */
@Entity
@Table(name = "pointPolicy")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointEntity extends BaseTime {

    // ========== 1. 테이블 필드 설계 ==========

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private int pointId;                   // 포인트번호 (PK)

    /**
     * 포인트명 필드
     * - 포인트 정책의 이름
     * - 예: "출석 포인트", "약속 이행 포인트", "목장 업무 포인트"
     */
    @Column(nullable = false, length = 40) // NOT NULL, 최대 40자
    private String pointName;              // 포인트명

    /**
     * 지급 포인트 필드
     * - 해당 정책에 따라 지급되는 포인트 양
     * - 양수: 포인트 적립
     * - 음수: 포인트 차감
     */
    @Column(nullable = false)
    private int updatePoint;               // 지급 포인트

    // ========== 2. 양방향 연결 ==========
    // 상위 엔티티가 하위 엔티티를 참조하는 관계
    // (현재 없음)

    // ========== 3. 단방향 연결 ==========
    // 하위 엔티티가 상위 엔티티를 참조하는 관계
    // (현재 없음)

    // ========== 4. Entity -> Dto 변환 메서드 ==========
    /**
     * 엔티티를 DTO로 변환하는 메서드
     * @return PointDto 포인트 정책 데이터 전송 객체
     */
    public PointDto toDto() {
        return PointDto.builder()
                .pointId(this.pointId)
                .pointName(this.pointName)
                .updatePoint(this.updatePoint)
                .createDate(this.getCreateDate().toString())
                .updateDate(this.getUpdateDate().toString())
                .build();
    }
}