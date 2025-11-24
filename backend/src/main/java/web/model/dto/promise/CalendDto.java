package web.model.dto.promise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.common.CycleType;
import web.model.entity.promise.CalendEntity;
import web.model.entity.promise.PromEntity;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendDto {

    // 1. 테이블 설계
    private int calend_id;              // 캘린더번호 (PK)
    private CycleType calend_cycle;     // 반복주기
    private LocalDateTime calend_start; // 반복시작일
    private LocalDateTime calend_end;   // 반복종료일
    private int prom_id;                // 약속번호
    private String create_date;         // 생성일
    private String update_date;         // 수정일

    // 2. Dto -> Entity 변환 : C
    public CalendEntity toEntity(PromEntity promEntity) {
        return CalendEntity.builder()
                .calend_id( calend_id )
                .calend_cycle( calend_cycle )
                .calend_start( calend_start )
                .calend_end( calend_end )
                .promEntity( promEntity )
                .build();
    }

}
