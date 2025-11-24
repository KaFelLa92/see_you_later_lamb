package web.model.dto.farm;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.farm.OwnerEntity;
import web.model.entity.farm.WorkEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkDto {

    // 1. 테이블 설계
    private int work_id;                    // 목장업무번호 (PK)
    private String work_name;               // 목장업무명
    private int work_state;                 // 업무상태 -1 : 기한종료 , 0 : 미실행 , 1 : 완료
    private LocalDateTime work_end_date;    // 업무안내종료일시 : 업무를 완료했거나 기한종료된 시간
    private int owner_id;                   // 목장주번호 (FK)
    private String create_date;             // 생성일
    private String update_date;             // 수정일

    // 2. Dto -> Entity 변환 : C
    private WorkEntity toEntity (OwnerEntity ownerEntity) {
        return WorkEntity.builder()
                .work_id(work_id)
                .work_name(work_name)
                .work_state(work_state)
                .work_end_date(work_end_date)
                .ownerEntity(ownerEntity)
                .build();
    }

}
