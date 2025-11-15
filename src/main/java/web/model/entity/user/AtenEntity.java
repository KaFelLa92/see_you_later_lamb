package web.model.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.user.AtenDto;
import web.model.entity.BaseTime;

import java.time.LocalDate;

@Entity
@Table( name = "attendance" )
@Data @Builder
@AllArgsConstructor
@NoArgsConstructor
public class AtenEntity extends BaseTime {

    // 1. 테이블 설계
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private int aten_id;                                    // 출석번호
    @Column( nullable = false )
    private LocalDate aten_date;                            // 출석일시

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계
    @ManyToOne( fetch = FetchType.LAZY ) // 캐스케이드 있으면 같이 삭제될 수 있음
    @JoinColumn( name = "user_id" ) // FK 필드명 (PK 필드명과 동일하게)
    private UsersEntity usersEntity;

    // 4. Entity -> Dto 변환 : R
    public AtenDto toDto () {
        return AtenDto.builder()
                .aten_id( this.aten_id )
                .aten_date( this.aten_date )
                .user_id( this.usersEntity.getUser_id() )
                .create_date( this.getCreate_date().toString() )
                .update_date( this.getUpdate_date().toString() )
                .build();
    }
}
