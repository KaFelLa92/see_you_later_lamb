package web.model.entity.lamb;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.lamb.ShepDto;
import web.model.entity.BaseTime;
import web.model.entity.user.UsersEntity;

///  양과 사용자를 묶어주는 엔티티

@Entity
@Table( name = "shepherd" )
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShepEntity extends BaseTime {

    // 1. 테이블 설계
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private int shep_id;                    // 양치기번호 (PK)
    @Column( nullable = false , length = 30 )
    private String shep_name;               // 양별명 , 사용자가 지어준 양의 별명
    private int shep_hunger;                // 양배고픔 -1 : 배고픔 , 0 : 보통 , 1 : 배부름
    private int shep_fur;                   // 양털상태 -1 : 털 많음 , 0 : 털 보통 , 1 : 털 없음
    private int shep_exist;                 // 양존재여부 1 : 울타리에 있음 , 0 : 목장에 있음 , -1 : 늑대에 쫓기는 중(사용자 소유가 아님)

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "lamb_id" )
    private LambEntity lambEntity;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "user_id" )
    private UsersEntity usersEntity;

    // 4. Entity -> Dto 변환 : R
    public ShepDto toDto () {
        return ShepDto.builder()
                .shep_id( this.shep_id )
                .shep_name( this.shep_name )
                .shep_hunger( this.shep_hunger )
                .shep_fur( this.shep_fur )
                .shep_exist( this.shep_exist )
                .lamb_id( this.lambEntity.getLamb_id() )
                .user_id( this.usersEntity.getUser_id() )
                .create_date( this.getCreate_date().toString() )
                .update_date( this.getUpdate_date().toString() )
                .build();
    }

}
