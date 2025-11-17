package web.model.entity.lamb;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name = "lambChar" )
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambCharEntity {

    // 1. 테이블 설계
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private int char_id;                // 양특성번호
    private String char_name;           // 특성명
    private String char_desc;           // 특성설명
    private String effect_type;         // 효과분류
    private String effect_value;        // 효과값
    private int is_active;              // 활성화여부

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계

    // 4. Entity -> Dto 변환 : R


}
