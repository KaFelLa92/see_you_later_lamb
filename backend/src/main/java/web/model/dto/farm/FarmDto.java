package web.model.dto.farm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.farm.FarmEntity;
import web.model.entity.user.UsersEntity;

/**
 * 목장 정보(Farm Information) DTO
 * 목장 정보를 전달하기 위한 데이터 전송 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmDto {

    // ========== 1. 필드 설계 ==========

    private int farmId;                    // 목장번호 (PK)
    private String farmName;               // 목장명
    private String farmInfo;               // 목장 소개

    /**
     * 최대 양 숫자
     * - 목장에서 기를 수 있는 최대 양의 수
     * - 기본값: 10마리
     */
    private int maxLamb;                   // 최대 양 숫자

    private int farmCost;                  // 목장 구매 비용 (포인트)
    private int userId;                    // 사용자번호 (FK)
    private String createDate;             // 생성일
    private String updateDate;             // 수정일

    // ========== 2. Dto -> Entity 변환 메서드 ==========
    /**
     * DTO를 엔티티로 변환하는 메서드 (생성용)
     * @param usersEntity 사용자 엔티티
     * @return FarmEntity 목장 정보 엔티티
     */
    public FarmEntity toEntity(UsersEntity usersEntity) {
        return FarmEntity.builder()
                .farmId(farmId)
                .farmName(farmName)
                .farmInfo(farmInfo)
                .maxLamb(maxLamb)
                .farmCost(farmCost)
                .usersEntity(usersEntity)
                .build();
    }
}