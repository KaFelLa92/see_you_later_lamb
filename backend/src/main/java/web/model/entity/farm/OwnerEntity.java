package web.model.entity.farm;

import org.apache.ibatis.annotations.Many;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.farm.OwnerDto;
import web.model.entity.BaseTime;
import web.model.entity.user.UsersEntity;

@Entity
@Table( name = "farmOwner" )
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OwnerEntity extends BaseTime {

    // 1. 테이블 설계
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private int owner_id;                   // 목장주번호 (PK)
    @Column( nullable = false , length = 50 )
    private String owner_name;              // 목장별명

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "farm_id" )
    private FarmEntity farmEntity;

    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "user_id" )
    private UsersEntity usersEntity;

    // 4. Entity -> Dto 변환 : R
    public OwnerDto toDto () {
        return OwnerDto.builder()
                .owner_id( this.owner_id )
                .owner_name( this.owner_name )
                .farm_id( this.farmEntity.getFarm_id() )
                .user_id( this.usersEntity.getUser_id() )
                .create_date( this.getCreate_date().toString() )
                .update_date( this.getUpdate_date().toString() )
                .build();
    }

}
