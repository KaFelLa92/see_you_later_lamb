package web.model.dto.lamb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.common.LambRank;
import web.model.entity.lamb.LambCharEntity;
import web.model.entity.lamb.LambEntity;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambDto {

    // 1. 테이블 설계
    private int lamb_id;        // 양번호
    private String lamb_name;   // 양품종 ex : 풍성해양 , 겁없어양 , 배고파양 , 전기양 , 콜리닮았어양
    private String lamb_info;   // 양소개
    private LambRank lamb_rank; // 양등급 1 : 일반 , 2 : 희귀 , 3 : 특급 , 4 : 전설
    private String lamb_path;   // 양일러스트경로 (파일명도 포함)
    private int char_id;        // 양 특성번호 (FK)
    private String create_date; // 생성일
    private String update_date; // 수정일

    // 2. Dto -> Entity 변환 : C
    public LambEntity toEntity(LambCharEntity lambCharEntity) {
        return LambEntity.builder()
                .lamb_id(lamb_id)
                .lamb_name(lamb_name)
                .lamb_info(lamb_info)
                .lamb_rank(lamb_rank)
                .lamb_path(lamb_path)
                .lambCharEntity(lambCharEntity)
                .build();
    }


}
