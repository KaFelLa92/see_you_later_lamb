package web.model.dto.lamb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambDto {

    // 1. 테이블 설계
    private int lamb_id;        // 양번호
    private String lamb_name;   // 양품종
    private String lamb_info;   // 양소개

    private int char_id;        // 양 특성 (FK)

    // 2. Dto -> Entity 변환 : C
}
