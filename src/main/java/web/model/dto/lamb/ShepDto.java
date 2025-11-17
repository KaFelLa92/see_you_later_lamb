package web.model.dto.lamb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.lamb.LambEntity;
import web.model.entity.lamb.ShepEntity;
import web.model.entity.user.UsersEntity;

///  양과 사용자를 묶어주는 테이블

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShepDto {

    // 1. 테이블 설계
    private int shep_id;                    // 양치기번호 (PK)
    private String shep_name;               // 양별명 , 사용자가 지어준 양의 별명
    private int shep_hunger;                // 양배고픔 -1 : 배고픔 , 0 : 보통 , 1 : 배부름
    private int shep_fur;                   // 양털상태 -1 : 털 많음 , 0 : 털 보통 , 1 : 털 없음
    private int shep_exist;                 // 양존재여부 1 : 울타리에 있음 , 0 : 목장에 있음 , -1 : 늑대에 쫓기는 중(사용자 소유가 아님)
    private int lamb_id;                    // 양번호 (FK)
    private int user_id;                    // 사용자번호 (FK)
    private String create_date;             // 생성일
    private String update_date;             // 수정일

    // 2. Dto -> Entity 변환 : C
    public ShepEntity toEntity(LambEntity lambEntity, UsersEntity usersEntity) {
        return ShepEntity.builder()


                .build();
    }
}
