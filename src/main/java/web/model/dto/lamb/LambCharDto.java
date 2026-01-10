package web.model.dto.lamb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.lamb.LambCharEntity;

/**
 * 양 특성(Lamb Characteristic) DTO
 * 양의 특성 정보를 전달하기 위한 데이터 전송 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambCharDto {

    // ========== 1. 필드 설계 ==========

    private int charId;                // 양특성번호 (PK)
    private String charName;           // 특성명
    private String charDesc;           // 특성 설명
    private String effectType;         // 효과 분류 (int/json 파싱 필요)
    private String effectValue;        // 효과 값
    private int isActive;              // 활성화 여부 (1: 활성화, 0: 비활성화)
    private String createDate;         // 생성일
    private String updateDate;         // 수정일

    // ========== 2. Dto -> Entity 변환 메서드 ==========
    /**
     * DTO를 엔티티로 변환하는 메서드 (생성용)
     * @return LambCharEntity 양 특성 엔티티
     */
    public LambCharEntity toEntity() {
        return LambCharEntity.builder()
                .charId(charId)
                .charName(charName)
                .charDesc(charDesc)
                .effectType(effectType)
                .effectValue(effectValue)
                .isActive(isActive)
                .build();
    }
}