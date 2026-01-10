package web.model.dto.point;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.point.PointEntity;

/**
 * 포인트 정책(Point Policy) DTO
 * 포인트 적립 공식을 관리하는 데이터 전송 객체
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointDto {

    // ========== 1. 필드 설계 ==========

    private int pointId;                   // 포인트번호 (PK)
    private String pointName;              // 포인트명
    private int updatePoint;               // 지급 포인트 (양수: 적립, 음수: 차감)
    private String createDate;             // 생성일
    private String updateDate;             // 수정일

    // ========== 2. Dto -> Entity 변환 메서드 ==========
    /**
     * DTO를 엔티티로 변환하는 메서드 (생성용)
     * @return PointEntity 포인트 정책 엔티티
     */
    public PointEntity toEntity() {
        return PointEntity.builder()
                .pointId(pointId)
                .pointName(pointName)
                .updatePoint(updatePoint)
                .build();
    }
}