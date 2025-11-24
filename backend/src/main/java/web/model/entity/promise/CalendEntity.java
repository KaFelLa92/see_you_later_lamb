package web.model.entity.promise;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.promise.CalendDto;
import web.model.entity.BaseTime;
import web.model.entity.common.CycleType;

import java.time.LocalDateTime;

@Entity
@Table( name = "calender" )
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendEntity extends BaseTime {

    // 1. 테이블 설계
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private int calend_id;              // 캘린더번호 (PK)
    @Enumerated(EnumType.STRING)
    private CycleType calend_cycle;     // 반복주기
    @Column(nullable = false)
    private LocalDateTime calend_start; // 반복시작일
    @Column(nullable = false)
    private LocalDateTime calend_end;   // 반복종료일

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "prom_id" )
    private PromEntity promEntity;

    // 4. Entity -> Dto 변환 : R
    public CalendDto toDto() {
        return CalendDto.builder()
                .calend_id( this.calend_id )
                .calend_cycle( this.calend_cycle )
                .calend_start( this.calend_start)
                .calend_end( this.calend_end )
                .prom_id( this.promEntity.getProm_id() )
                .create_date( this.getCreate_date().toString() )
                .update_date( this.getUpdate_date().toString() )
                .build();
    }

}
