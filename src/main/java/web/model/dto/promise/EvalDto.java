package web.model.dto.promise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.promise.EvalEntity;
import web.model.entity.promise.ShareEntity;
import web.model.entity.promise.TempEntity;
import web.model.entity.user.UsersEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvalDto {

    // 1. 테이블 설계
    private int eval_id;                // 약속평가자번호 (PK)
    private int user_id;                // 사용자번호 (FK)
    private int temp_id;                // 임시사용자번호(FK)
    private int share_id;               // 약속공유번호 (FK)
    private String create_date;         // 생성일
    private String update_date;         // 수정일

    // 2. Dto -> Entity 변환 : C
    public EvalEntity toEntity(UsersEntity usersEntity , TempEntity tempEntity, ShareEntity shareEntity) {
        return EvalEntity.builder()

                .build();
    }

}
