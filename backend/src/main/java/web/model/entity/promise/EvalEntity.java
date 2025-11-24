package web.model.entity.promise;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.promise.EvalDto;
import web.model.entity.BaseTime;
import web.model.entity.user.UsersEntity;

@Entity
@Table( name = "eval" )
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvalEntity extends BaseTime {

    // 1. 테이블 설계
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int eval_id;                // 약속평가자번호 (PK)

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계
    // 사용자(회원)
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "user_id" )
    private UsersEntity usersEntity;

    // 임시사용자(비회원)
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "temp_id" )
    private TempEntity tempEntity;

    // 약속공유
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "share_id" )
    private ShareEntity shareEntity;

    // 4. Entity -> Dto 변환 : R
    public EvalDto toDto() {
        return EvalDto.builder()
                .eval_id( this.eval_id )
                .user_id( this.usersEntity.getUser_id() )
                .temp_id( this.tempEntity.getTemp_id() )
                .share_id( this.shareEntity.getShare_id() )
                .create_date( this.getCreate_date().toString() )
                .update_date( this.getUpdate_date().toString() )
                .build();
    }

}
