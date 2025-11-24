package web.model.entity.farm;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.farm.FarmDto;
import web.model.entity.BaseTime;
import web.model.entity.user.UsersEntity;

@Entity
@Table( name = "farmInfo" )
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmEntity extends BaseTime {

    // 1. 테이블 설계
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private int farm_id;                    // 목장번호
    @Column( nullable = false , length = 50 )
    private String farm_name;               // 목장명
    @Column( nullable = false )
    private String farm_info;               // 목장소개
    @Column( nullable = false )
    @Builder.Default
    private int max_lamb = 10;              // 최대양숫자 : 디폴트 10마리
    @Column( nullable = false )
    private int farm_cost;                  // 목장구매비용

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "user_id" )
    private UsersEntity usersEntity;

    // 4. Entity -> Dto 변환 : R
    public FarmDto toDto() {
        return FarmDto.builder()
                .farm_id( this.farm_id )
                .farm_name( this.farm_name )
                .farm_info( this.farm_info )
                .max_lamb( this.max_lamb )
                .farm_cost( this.farm_cost )
                .user_id( this.usersEntity.getUser_id() )
                .create_date( this.getCreate_date().toString() )
                .update_date( this.getUpdate_date().toString() )
                .build();
    }


}
