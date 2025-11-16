package web.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.common.LangType;
import web.model.entity.common.TrafficType;
import web.model.entity.user.SetEntity;
import web.model.entity.user.UsersEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetDto {

    // 1. 테이블 설계
    private int set_id;                 // 설정번호 (PK)
    private int set_remind;             // 약속리마인드 약속시간 기준으로 '몇 분 전'에 알람줄 것인가 (개인 설정) 0일 경우 리마인드 설정 해제
    private int set_work;               // 업무표시 시간마다 들어오는 목장 업무를 플레이할 것인가의 여부 0 : 플레이안함 , 1 : 플레이함
    private TrafficType set_traffic;    // 우선교통수단
    private LangType set_language;      // 언어설정
    private int user_id;                // 사용자번호 (FK)
    private String create_date;         // 생성일
    private String update_date;         // 수정일

    // 2. Dto -> Entity 변환 : C
    public SetEntity toEntity(UsersEntity usersEntity) {
        return SetEntity.builder()
                .set_id( set_id )
                .set_remind( set_remind )
                .set_work( set_work )
                .set_traffic( set_traffic )
                .set_language( set_language )
                .usersEntity( usersEntity )
                .build();
    }

}

