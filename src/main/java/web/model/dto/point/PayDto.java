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

///  포인트 적립 공식을 사용자와 이어주는 DTO

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayDto {

    // 1. 테이블 설계
    private int pay_id;                     // 포인트지급번호 (PK)
    private int aten_id;                    // 출석번호 (FK)
    private int share_id;                   // 약속공유번호 (FK)
    private int work_id;                    // 목장업무번호 (FK)
    private int farm_id;                    // 목장번호 (FK)
    private int point_id;                   // 포인트번호 (FK)
    private String create_date;             // 생성일
    private String update_date;             // 수정일

    // 2. Dto -> Entity 변환 : C
    public PayEntity toEntity(AtenEntity atenEntity ,
                              ShareEntity shareEntity ,
                              WorkEntity workEntity ,
                              FarmEntity farmEntity ,
                              PointEntity pointEntity) {
        return PayEntity.builder()
                .pay_id( pay_id )
                .atenEntity(atenEntity)
                .shareEntity(shareEntity)
                .workEntity(workEntity)
                .farmEntity(farmEntity)
                .pointEntity(pointEntity)
                .build();
    }

}
