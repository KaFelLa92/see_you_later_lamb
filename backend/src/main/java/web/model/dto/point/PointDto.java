package web.model.dto.point;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.point.PointEntity;

///  포인트 적립 공식을 관리하는 DTO

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointDto {

    // 1. 테이블 설계
    private int point_id;                   // 포인트번호 (PK)
    private String point_name;              // 포인트명
    private int update_point;               // 지급포인트
    private String create_date;             // 생성일
    private String update_date;             // 수정일

    // 2. Dto -> Entity 변환 : C
    public PointEntity toEntity () {
        return PointEntity.builder()
                .point_id(point_id)
                .point_name(point_name)
                .update_point(update_point)
                .build();
    }
}
