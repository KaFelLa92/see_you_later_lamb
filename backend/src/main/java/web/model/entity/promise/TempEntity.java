package web.model.entity.promise;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.promise.TempDto;
import web.model.entity.BaseTime;

@Entity
@Table( name = "temp" )
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempEntity extends BaseTime {

    // 1. 테이블 설계
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int temp_id;                        // 임시사용자번호 (PK)
    @Column( nullable = false , length = 30 )
    @Builder.Default
    private String temp_name = "지나가던 양치기";  // 임시사용자명

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계

    // 4. Entity -> Dto 변환 : R
    public TempDto toDto () {
        return TempDto.builder()
                .temp_id( this.temp_id )
                .temp_name( this.temp_name )
                .create_date( this.getCreate_date().toString() )
                .update_date( this.getUpdate_date().toString() )
                .build();
    }
}
