package web.model.dto.lamb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambCharDto {

    // 1. 테이블 설계
    private int char_id;                // 양특성번호
    private String char_name;           // 특성명
    private String char_desc;           // 특성설명
    private String effect_type;         // 효과분류
    private String effect_value;        // 효과값
    private int is_active;              // 활성화여부
    private String create_date;         // 생성일
    private String update_date;         // 수정일

    // 2. Dto -> Entity 변환 : C

}
