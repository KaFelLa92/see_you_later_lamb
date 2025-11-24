package web.model.dto.promise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.promise.TempEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempDto {

    // 1. 테이블 설계
    private int temp_id;                // 임시사용자번호 (PK)
    private String temp_name;           // 임시사용자명
    private String create_date;         // 생성일
    private String update_date;         // 수정일

    // 2. Dto -> Entity 변환 : C
    public TempEntity toEntity() {
        return TempEntity.builder()
                .temp_id(temp_id)
                .temp_name(temp_name)
                .build();
    }
}
