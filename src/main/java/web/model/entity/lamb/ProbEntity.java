package web.model.entity.lamb;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.promise.ShareEntity;

@Entity
@Table( name = "probability" )
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProbEntity {

    // 1. 테이블 설계
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private int prob_id;                    // 등장확률번호 (PK)
    private int prob_lamb;                  // 양등장확률
    private int prob_wolf;                  // 늑대등장확률
    private int prob_rare;                  // 희귀등급등장확률 , 일반양 등장확률 빼고, 해당 레코드만큼 확률 보정이 들어감

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "share_id" )
    private ShareEntity shareEntity;

    // 4. Entity -> Dto 변환 : R

}
