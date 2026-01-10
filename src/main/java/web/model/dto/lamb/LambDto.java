package web.model.dto.lamb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.common.LambRank;
import web.model.entity.lamb.LambCharEntity;
import web.model.entity.lamb.LambEntity;

/**
 * 양 정보(Lamb Information) DTO
 * 양의 기본 정보를 전달하기 위한 데이터 전송 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambDto {

    // ========== 1. 필드 설계 ==========

    private int lambId;        // 양번호 (PK)

    /**
     * 양 품종
     * 예: 풍성해양, 겁없어양, 배고파양, 전기양, 콜리닮았어양
     */
    private String lambName;   // 양 품종

    private String lambInfo;   // 양 소개

    /**
     * 양 등급
     * 1: 일반(COMMON)
     * 2: 희귀(RARE)
     * 3: 특급(EPIC)
     * 4: 전설(LEGENDARY)
     */
    private LambRank lambRank; // 양 등급

    private String lambPath;   // 양 일러스트 경로 (파일명 포함)
    private int charId;        // 양 특성번호 (FK)
    private String createDate; // 생성일
    private String updateDate; // 수정일

    // ========== 2. Dto -> Entity 변환 메서드 ==========
    /**
     * DTO를 엔티티로 변환하는 메서드 (생성용)
     * @param lambCharEntity 양 특성 엔티티
     * @return LambEntity 양 정보 엔티티
     */
    public LambEntity toEntity(LambCharEntity lambCharEntity) {
        return LambEntity.builder()
                .lambId(lambId)
                .lambName(lambName)
                .lambInfo(lambInfo)
                .lambRank(lambRank)
                .lambPath(lambPath)
                .lambCharEntity(lambCharEntity)
                .build();
    }
}