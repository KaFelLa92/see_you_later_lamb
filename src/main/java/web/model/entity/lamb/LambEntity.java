package web.model.entity.lamb;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name = "lambInfo" )
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambEntity {

    // 1. 테이블 설계

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계

    // 4. Entity -> Dto 변환 : R
}
