package web.model.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.user.FrenDto;
import web.model.entity.BaseTime;

@Entity
@Table( name = "friend" )
@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class FrenEntity extends BaseTime {

    // 1. 테이블 설계
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private int fren_id;        // 친구번호
    @Column( nullable = false )
    private int fren_state = 0;     // 친구 상태    1 : 현재 친구 , 0 : 친구 신청 중 , -1 : 더 이상 친구 아님(삭제)

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "fren_offer" , nullable = false )
    private UsersEntity offerUser;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "fren_receiver" , nullable = false )
    private UsersEntity receiverUser;

    // 4. Entity -> Dto 변환 : R
    public FrenDto toDto () {
        return FrenDto.builder()
                .fren_id( this.fren_id )
                .fren_state( this.fren_state )
                .fren_offer( this.offerUser.getUser_id() )
                .fren_receiver( this.receiverUser.getUser_id() )
                .create_date( this.getCreate_date().toString() )
                .update_date( this.getUpdate_date().toString() )
                .build();
    }

}
