package web.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.user.AtenEntity;
import web.model.entity.user.UsersEntity;

import java.time.LocalDate;

@Data @Builder
@AllArgsConstructor
@NoArgsConstructor
public class AtenDto {

    // 1. 테이블 설계
    private int aten_id;            // 출석번호 (PK)
    private LocalDate aten_date;    // 출석일시
    private int user_id;            // 사용자번호 (FK)
    private String create_date;     // 생성일
    private String update_date;     // 수정일

    // 2. Dto -> Entity 변환 : C
    public AtenEntity toEntity(UsersEntity usersEntity) {
        return AtenEntity.builder()
                .aten_id( aten_id )
                .aten_date( aten_date )
                .usersEntity( usersEntity )
                .build();

    }

}
