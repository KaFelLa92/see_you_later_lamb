package web.model.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.user.SetDto;
import web.model.entity.BaseTime;
import web.model.entity.common.LangType;
import web.model.entity.common.TrafficType;

@Entity
@Table( name = "setting" )
@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetEntity extends BaseTime {

    // 1. 테이블 설계
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private int set_id;         // 설정번호 (PK)
    @Column( nullable = false )
    @Builder.Default
    private int set_remind = 0; // 약속 리마인드 약속시간 기준으로 '몇 분 전'에 알람줄 것인가 (개인 설정) 0일 경우 리마인드 설정 해제
    @Column( nullable = false )
    @Builder.Default
    private int set_work = 1;   // 업무표시 시간마다 들어오는 목장 업무를 플레이할 것인가의 여부 0 : 플레이안함 , 1 : 플레이함
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TrafficType set_traffic = TrafficType.SUBWAY_AND_BUS;   // 우선교통수단 디폴트 지하철+버스
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LangType set_language = LangType.KOREAN;                    // 언어설정 디폴트 한국어

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "user_id" )
    private UsersEntity usersEntity;

    // 4. Entity -> Dto 변환 : R
    public SetDto toDto () {
        return SetDto.builder()
                .set_id( this.set_id )
                .set_remind( this.set_remind )
                .set_work( this.set_work )
                .set_traffic( this.set_traffic )
                .set_language( this.set_language )
                .user_id( this.usersEntity.getUser_id() )
                .create_date( this.getCreate_date().toString() )
                .update_date( this.getUpdate_date().toString() )
                .build();
    }
}
