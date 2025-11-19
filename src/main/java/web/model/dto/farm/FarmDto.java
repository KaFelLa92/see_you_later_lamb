package web.model.dto.farm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.farm.FarmEntity;
import web.model.entity.user.UsersEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmDto {

    // 1. 테이블 설계
    private int farm_id;                    // 목장번호 (PK)
    private String farm_name;               // 목장명
    private String farm_info;               // 목장소개
    private int max_lamb;                   // 최대양숫자 : 디폴트 10마리
    private int farm_cost;                  // 목장구매비용
    private int user_id;                    // 사용자번호 (FK)
    private String create_date;             // 생성일
    private String update_date;             // 수정일

    // 2. Dto -> Entity 변환 : C
    public FarmEntity toEntity (UsersEntity usersEntity) {
        return FarmEntity.builder()
                .farm_id(farm_id)
                .farm_name(farm_name)
                .farm_info(farm_info)
                .max_lamb(max_lamb)
                .farm_cost(farm_cost)
                .usersEntity(usersEntity)
                .build();

    }

}
