package web.model.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.BaseTime;
import web.model.entity.common.LangType;

@Entity
@Table( name = "setting" )
@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetEntity extends BaseTime {

    // 1. 테이블 설계
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private int set_id;     // 설정번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LangType language;

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계

    // 4. Entity -> Dto 변환 : R
}
