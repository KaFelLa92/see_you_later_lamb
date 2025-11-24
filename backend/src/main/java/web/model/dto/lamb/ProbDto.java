package web.model.dto.lamb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.lamb.ProbEntity;
import web.model.entity.promise.ShareEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProbDto {

    // 1. 테이블 설계
    private int prob_id;                    // 등장확률번호 (PK)
    private int prob_lamb;                  // 양등장확률
    private int prob_wolf;                  // 늑대등장확률
    private int prob_rare;                  // 희귀등급등장확률 , 일반양 등장확률 빼고, 해당 레코드만큼 확률 보정이 들어감
    private int share_id;                   // 약속공유번호 (FK), 약속확인과 약속점수 기반으로 확률을 매기기
    private String create_date;             // 생성일
    private String update_date;             // 수정일

    // 2. Dto -> Entity 변환 : C
    public ProbEntity toEntity(ShareEntity shareEntity) {
        return ProbEntity.builder()
                .prob_id(prob_id)
                .prob_lamb(prob_lamb)
                .prob_wolf(prob_wolf)
                .prob_rare(prob_rare)
                .shareEntity(shareEntity)
                .build();
    }
}
