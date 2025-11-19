package web.model.dto.farm;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.farm.FarmEntity;
import web.model.entity.farm.OwnerEntity;
import web.model.entity.user.UsersEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OwnerDto {

    // 1. 테이블 설계
    private int owner_id;                   // 목장주번호 (PK)
    private String owner_name;              // 목장별명
    private int farm_id;                    // 목장번호 (FK)
    private int user_id;                    // 사용자번호 (FK)
    private LocalDateTime create_date;             // 생성일
    private LocalDateTime update_date;             // 수정일

    // 2. Dto -> Entity 변환 : C
    public OwnerEntity toEntity(FarmEntity farmEntity, UsersEntity usersEntity) {
        return OwnerEntity.builder()
                .owner_id(owner_id)
                .owner_name(owner_name)
                .farmEntity(farmEntity)
                .usersEntity(usersEntity)
                .build();
    }

}
