package web.model.entity.lamb;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.lamb.LambCharDto;
import web.model.entity.BaseTime;

@Entity
@Table( name = "lambChar" )
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambCharEntity extends BaseTime {

    // 1. 테이블 설계
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private int char_id;                // 양특성번호
    @Column( nullable = false , length = 30 )
    private String char_name;           // 특성명
    @Column( nullable = false  )
    private String char_desc;           // 특성설명
    @Column( nullable = false , length = 30 )
    private String effect_type;         // 효과분류 (int/json) 파싱해서 쓰기
    @Column( nullable = false )
    private String effect_value;        // 효과값
    private int is_active;              // 활성화여부 (1 or 0)

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계

    // 4. Entity -> Dto 변환 : R
    public LambCharDto toDto () {
        return LambCharDto.builder()
                .char_id( this.char_id )
                .char_name( this.char_name)
                .char_desc( this.char_desc )
                .effect_type( this.effect_type )
                .effect_value( this.effect_value )
                .is_active( this.is_active )
                .create_date( this.getCreate_date().toString() )
                .update_date( this.getUpdate_date().toString() )
                .build();
    }


}
