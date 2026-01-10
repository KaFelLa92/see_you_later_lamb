package web.model.dto.farm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.farm.FarmEntity;
import web.model.entity.farm.OwnerEntity;
import web.model.entity.user.UsersEntity;

/**
 * 목장주(Farm Owner) DTO
 * 목장과 사용자를 연결하는 데이터 전송 객체
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OwnerDto {

    // ========== 1. 필드 설계 ==========

    private int ownerId;                   // 목장주번호 (PK)
    private String ownerName;              // 목장 별명 (사용자가 지어준 이름)
    private int farmId;                    // 목장번호 (FK)
    private int userId;                    // 사용자번호 (FK)
    private String createDate;             // 생성일
    private String updateDate;             // 수정일

    // ========== 2. Dto -> Entity 변환 메서드 ==========
    /**
     * DTO를 엔티티로 변환하는 메서드 (생성용)
     * @param farmEntity 목장 정보 엔티티
     * @param usersEntity 사용자 엔티티
     * @return OwnerEntity 목장주 엔티티
     */
    public OwnerEntity toEntity(FarmEntity farmEntity, UsersEntity usersEntity) {
        return OwnerEntity.builder()
                .ownerId(ownerId)
                .ownerName(ownerName)
                .farmEntity(farmEntity)
                .usersEntity(usersEntity)
                .build();
    }
}