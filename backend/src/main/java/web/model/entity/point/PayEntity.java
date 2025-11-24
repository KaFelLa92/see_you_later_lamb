package web.model.entity.point;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.point.PayDto;
import web.model.entity.BaseTime;
import web.model.entity.farm.FarmEntity;
import web.model.entity.farm.WorkEntity;
import web.model.entity.promise.ShareEntity;
import web.model.entity.user.AtenEntity;
import web.model.entity.user.UsersEntity;

///  포인트 적립 공식을 사용자와 이어주는 엔티티

@Entity
@Table( name = "pointPay" )
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayEntity extends BaseTime {

    // 1. 테이블 설계
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private int pay_id;                     // 포인트지급번호 (PK)

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계
    // 단방향연결 : 출석 (출석 시 포인트 지급)
    @ManyToOne( fetch = FetchType.LAZY ) // 캐스케이드 있으면 같이 삭제될 수 있음
    @JoinColumn( nullable = true , name = "aten_id" ) // FK 필드명 (PK 필드명과 동일하게)
    private AtenEntity atenEntity;

    // 단방향연결 : 약속공유 (약속 잘 수행 시 포인트 지급)
    @ManyToOne( fetch = FetchType.LAZY ) // 캐스케이드 있으면 같이 삭제될 수 있음
    @JoinColumn( nullable = true , name = "share_id" ) // FK 필드명 (PK 필드명과 동일하게)
    private ShareEntity shareEntity;

    // 단방향연결 : 목장업무 (업무 수행시 포인트 지급)
    @ManyToOne( fetch = FetchType.LAZY ) // 캐스케이드 있으면 같이 삭제될 수 있음
    @JoinColumn( nullable = true , name = "work_id" ) // FK 필드명 (PK 필드명과 동일하게)
    private WorkEntity workEntity;

    // 단방향연결 : 목장 (목장 구매 관련)
    @ManyToOne( fetch = FetchType.LAZY ) // 캐스케이드 있으면 같이 삭제될 수 있음
    @JoinColumn( nullable = true , name = "farm_id" ) // FK 필드명 (PK 필드명과 동일하게)
    private FarmEntity farmEntity;

    // 단방향연결 : 포인트정책
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( nullable = true , name = "point_id" )
    private PointEntity pointEntity;

    // 4. Entity -> Dto 변환 : R

    public PayDto toDto () {
        return PayDto.builder()
                .pay_id( this.pay_id )
                .aten_id( this.atenEntity.getAten_id() )
                .share_id( this.shareEntity.getShare_id() )
                .work_id( this.workEntity.getWork_id() )
                .farm_id( this.farmEntity.getFarm_id() )
                .point_id( this.pointEntity.getPoint_id() )
                .create_date( this.getCreate_date().toString() )
                .update_date( this.getUpdate_date().toString() )
                .build();
    }
}
