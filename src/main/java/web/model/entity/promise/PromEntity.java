package web.model.entity.promise;

import jakarta.persistence.*;
import lombok.*;
import org.apache.ibatis.annotations.One;
import web.model.dto.promise.PromDto;
import web.model.entity.BaseTime;
import web.model.entity.user.UsersEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

///  포인트 적립 공식을 사용자와 이어주는 DTO

@Entity
@Table( name = "promise" )
@Data @Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromEntity extends BaseTime {

    // 1. 테이블 설계
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private int prom_id;                    // 약속번호 (PK)
    @Column( nullable = false , length = 100 )
    private String prom_title;              // 약속명
    private LocalDateTime prom_date;        // 약속일시 (시간 미정일 수 있으므로 널 가능)
    @Column( nullable = false )
    @Builder.Default
    private int prom_alert = 0;             // 약속알림시간 , 약속시간보다 이전에 알림을 하며, 분 단위로 측정한다. 0이면 알람 없음.
    private String prom_addr;               // 약속주소(장소)
    private String prom_addr_detail;        // 약속상세장소
    @Column(columnDefinition = "DOUBLE CHECK (prom_lat BETWEEN -90 AND 90)")
    private Double prom_lat;                // 약속장소위도
    @Column(columnDefinition = "DOUBLE CHECK (prom_lng BETWEEN -180 AND 180)")
    private Double prom_lng;                // 약속장소경도
    @Column( nullable = false )
    private String prom_text;               // 약속내용
    private String prom_memo;               // 약속메모/비고

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계
    // 양방향 연결: 약속 공유
    @OneToMany(mappedBy = "promEntity" , fetch = FetchType.LAZY )
    @ToString.Exclude
    @Builder.Default
    private List<ShareEntity> shareEntityList = new ArrayList<>();

    // 양방향 연결: 반복 약속
    @OneToMany(mappedBy = "promEntity" , fetch = FetchType.LAZY )
    @ToString.Exclude
    @Builder.Default
    private List<CalendEntity> calendEntityList = new ArrayList<>();

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계
    @ManyToOne( fetch = FetchType.LAZY ) // 캐스케이드 있으면 같이 삭제될 수 있음
    @JoinColumn( name = "user_id" ) // FK 필드명 (PK 필드명과 동일하게)
    private UsersEntity usersEntity;

    // 4. Entity -> Dto 변환 : R
    public PromDto toDto () {
        return PromDto.builder()
                .prom_id( this.prom_id )
                .prom_title( this.prom_title )
                .prom_date( this.prom_date )
                .prom_alert( this.prom_alert )
                .prom_addr( this.prom_addr )
                .prom_addr_detail( this.prom_addr_detail )
                .prom_lat( this.prom_lat )
                .prom_lng( this.prom_lng )
                .prom_text( this.prom_text )
                .prom_memo( this.prom_memo )
                .user_id( this.usersEntity.getUser_id() )
                .create_date( this.getCreate_date().toString() )
                .update_date( this.getUpdate_date().toString() )
                .build();
    }
}
