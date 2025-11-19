package web.model.entity.lamb;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.lamb.ProbDto;
import web.model.entity.BaseTime;
import web.model.entity.promise.ShareEntity;

@Entity
@Table( name = "probability" )
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProbEntity extends BaseTime {

    // 1. 테이블 설계
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private int prob_id;                    // 등장확률번호 (PK)
    @Column( nullable = false )
    private int prob_lamb;                  // 양등장확률
    @Column( nullable = false )
    private int prob_wolf;                  // 늑대등장확률
    @Column( nullable = false )
    private int prob_rare;                  // 희귀등급등장확률 , 일반양 등장확률 빼고, 해당 레코드만큼 확률 보정이 들어감

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "share_id" )
    private ShareEntity shareEntity;

    // 4. Entity -> Dto 변환 : R
    public ProbDto toDto () {
        return ProbDto.builder()
                .prob_id( this.prob_id )
                .prob_lamb( this.prob_lamb )
                .prob_wolf( this.prob_wolf )
                .prob_rare( this.prob_rare )
                .share_id( this.shareEntity.getShare_id() )
                .create_date( this.getCreate_date().toString() )
                .update_date( this.getUpdate_date().toString() )
                .build();
    }

}
