package web.model.entity.promise;

import jakarta.persistence.*;
import lombok.*;
import web.model.dto.promise.ShareDto;
import web.model.entity.BaseTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table( name = "share" )
@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareEntity extends BaseTime {

    // 1. 테이블 설계
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private int share_id;                                   // 약속공유번호 (PK)
    @Column( nullable = false, unique = true, length = 50 )
    private String share_token;                             // 공유 토큰 (URL에 사용)
    @Column( nullable = false )
    @Builder.Default
    private int share_check = 0;                            // 약속확인 -1 : 약속 어김 , 0 : 약속 이행 , 1 : 약속 잘 지킴
    @Column( nullable = false )
    @Builder.Default
    private int share_score = 3;                            // 약속점수  1~5점. 5점일수록 좋음 (디폴트3)
    @Builder.Default
    private String share_feedback = "약속 지켜줘서 고마워양!";  // 약속피드백

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계
    // 양방향 연결 : 약속 평가자
    @OneToMany(mappedBy = "shareEntity" , fetch = FetchType.LAZY )
    @ToString.Exclude
    @Builder.Default
    private List<EvalEntity> evalEntityList = new ArrayList<>();

    // 3. 단방향연결
    // 하위 엔티티가 상위 엔티티 참조 관계
    @ManyToOne( fetch = FetchType.LAZY )
    @JoinColumn( name = "prom_id" )
    private PromEntity promEntity;

    // 4. Entity 생성 시 토큰 자동 생성
    @PrePersist
    public void generateToken() {
        if (this.share_token == null || this.share_token.isEmpty()) {
            // UUID를 사용해 고유한 토큰 생성
            this.share_token = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        }
    }

    // 4. Entity -> Dto 변환 : R
    public ShareDto toDto() {
        return ShareDto.builder()
                .share_id( this.share_id )
                .share_token( this.share_token )
                .share_check( this.share_check )
                .share_score( this.share_score )
                .share_feedback( this.share_feedback )
                .prom_id( this.promEntity.getProm_id() )
                .create_date( this.getCreate_date().toString() )
                .update_date( this.getUpdate_date().toString() )
                .build();
    }
}
