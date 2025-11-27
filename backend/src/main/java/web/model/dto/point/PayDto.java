package web.model.dto.point;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.farm.FarmEntity;
import web.model.entity.farm.WorkEntity;
import web.model.entity.point.PayEntity;
import web.model.entity.point.PointEntity;
import web.model.entity.promise.ShareEntity;
import web.model.entity.user.AtenEntity;

/**
 * 포인트 지급(Point Pay) DTO
 * 포인트 적립 공식을 사용자와 연결하는 데이터 전송 객체
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayDto {

    // ========== 1. 필드 설계 ==========

    private int payId;                     // 포인트지급번호 (PK)
    private int atenId;                    // 출석번호 (FK)
    private int shareId;                   // 약속공유번호 (FK)
    private int workId;                    // 목장업무번호 (FK)
    private int farmId;                    // 목장번호 (FK)
    private int pointId;                   // 포인트번호 (FK)
    private String createDate;             // 생성일
    private String updateDate;             // 수정일

    // ========== 2. Dto -> Entity 변환 메서드 ==========
    /**
     * DTO를 엔티티로 변환하는 메서드 (생성용)
     * @param atenEntity 출석 엔티티
     * @param shareEntity 약속공유 엔티티
     * @param workEntity 목장업무 엔티티
     * @param farmEntity 목장 엔티티
     * @param pointEntity 포인트정책 엔티티
     * @return PayEntity 포인트 지급 엔티티
     */
    public PayEntity toEntity(AtenEntity atenEntity,
                              ShareEntity shareEntity,
                              WorkEntity workEntity,
                              FarmEntity farmEntity,
                              PointEntity pointEntity) {
        return PayEntity.builder()
                .payId(payId)
                .atenEntity(atenEntity)
                .shareEntity(shareEntity)
                .workEntity(workEntity)
                .farmEntity(farmEntity)
                .pointEntity(pointEntity)
                .build();
    }
}