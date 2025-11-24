package web.model.entity.point;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.point.PointDto;
import web.model.entity.BaseTime;

///  포인트 적립 공식을 관리하는 엔티티

@Entity
@Table( name = "pointPolicy" )
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointEntity extends BaseTime {

    // 1. 테이블 설계
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int point_id;                   // 포인트번호 (PK)
    @Column( nullable = false , length = 40 )
    private String point_name;              // 포인트명
    @Column( nullable = false )
    private int update_point;               // 지급포인트

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계

    // 4. Entity -> Dto 변환 : R
    public PointDto toDto () {
        return PointDto.builder()
                .point_id( this.point_id )
                .point_name( this.point_name )
                .update_point( this.update_point )
                .create_date( this.getCreate_date().toString() )
                .update_date( this.getUpdate_date().toString() )
                .build();
    }
}
