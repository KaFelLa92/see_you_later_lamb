package web.model.entity.farm;


import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.farm.WorkDto;
import web.model.entity.BaseTime;

@Entity
@Table( name = "farmWork" )
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkEntity extends BaseTime {

    // 1. 테이블 설계
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private int work_id;                    // 목장업무번호 (PK)
    @Column( nullable = false , length = 50 )
    private String work_name;               // 목장업무명
    @Column( nullable = false )
    @Builder.Default
    private int work_state = 0;             // 업무상태 -1 : 기한종료 , 0 : 미실행 , 1 : 완료 (디폴트 0)
    @Column( nullable = false )
    private LocalDateTime work_end_date;    // 업무안내종료일시 : 업무를 완료했거나 기한종료된 시간

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "owner_id" )
    private OwnerEntity ownerEntity;

    // 4. Entity -> Dto 변환 : R
    public WorkDto toDto () {
        return WorkDto.builder()
                .work_id( this.work_id )
                .work_name( this.work_name )
                .work_state( this.work_state )
                .work_end_date( this.work_end_date )
                .owner_id( this.ownerEntity.getOwner_id() )
                .create_date( this.getCreate_date().toString() )
                .update_date( this.getUpdate_date().toString() )
                .build();
    }

}
