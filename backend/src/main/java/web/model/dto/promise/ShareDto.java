package web.model.dto.promise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.promise.PromEntity;
import web.model.entity.promise.ShareEntity;
import web.model.entity.promise.TempEntity;
import web.model.entity.user.UsersEntity;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareDto {

    // 1. 테이블 설계
    private int share_id;                   // 약속공유번호 (PK)
    private String share_token;             // 공유 토큰
    private int share_check;                // 약속확인 -1 : 약속 어김 , 0 : 약속 이행 , 1 : 약속 잘 지킴
    private int share_score;                // 약속점수  1~5점. 5점일수록 좋음
    private String share_feedback;          // 약속피드백
    private int prom_id;                    // 약속번호 (FK)
    private String create_date;             // 생성일
    private String update_date;             // 수정일

    // 2. Dto -> Entity 변환 : C
    public ShareEntity toEntity(PromEntity promEntity, UsersEntity usersEntity, TempEntity tempEntity) {
        return ShareEntity.builder()
                .share_id(share_id)
                .share_token(share_token)
                .share_check(share_check)
                .share_score(share_score)
                .share_feedback(share_feedback)
                .promEntity(promEntity)
                .usersEntity(usersEntity)
                .build();
    }

}
