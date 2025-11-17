package web.model.entity.lamb;

import jakarta.persistence.*;
import lombok.*;
import org.apache.ibatis.annotations.Many;
import web.model.dto.lamb.LambDto;
import web.model.entity.BaseTime;
import web.model.entity.common.LambRank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table( name = "lambInfo" )
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambEntity extends BaseTime {

    // 1. 테이블 설계
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private int lamb_id;                            // 양번호
    @Column( nullable = false , length = 30 )
    private String lamb_name;                       // 양품종 ex : 풍성해양 , 겁없어양 , 배고파양 , 전기양 , 콜리닮았어양
    @Column( nullable = false )
    private String lamb_info;                       // 양소개
    @Column( nullable = false )
    @Builder.Default
    private LambRank lamb_rank = LambRank.COMMON;   // 양등급 1 : 일반 , 2 : 희귀 , 3 : 특급 , 4 : 전설

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계
    // 양방향 연결 : 양치기
    @OneToMany(mappedBy = "lambEntity" , fetch = FetchType.LAZY )
    @ToString.Exclude
    @Builder.Default
    private List<ShepEntity> shepEntityList = new ArrayList<>();

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "char_id" )
    private LambCharEntity lambCharEntity;

    // 4. Entity -> Dto 변환 : R
    public LambDto toDto () {
        return LambDto.builder()
                .lamb_id( this.lamb_id )
                .lamb_name( this.lamb_name )
                .lamb_info( this.lamb_info )
                .lamb_rank( this.lamb_rank )
                .char_id( this.lambCharEntity.getChar_id())
                .create_date( this.getCreate_date().toString() )
                .update_date( this.getUpdate_date().toString() )
                .build();
    }


}
