package web.model.entity.promise;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.promise.EvalDto;
import web.model.entity.BaseTime;
import web.model.entity.user.UsersEntity;

/**
 * 평가(Evaluation) 엔티티
 * 약속 평가자 정보를 관리하는 엔티티 클래스
 * 회원과 비회원 모두 약속을 평가할 수 있음
 */
@Entity
@Table(name = "eval")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvalEntity extends BaseTime {

    // ========== 1. 테이블 필드 설계 ==========

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private int evalId;                // 약속평가자번호 (PK)

    // ========== 2. 양방향 연결 ==========
    // 상위 엔티티가 하위 엔티티를 참조하는 관계
    // (현재 없음)

    // ========== 3. 단방향 연결 ==========
    // 하위 엔티티가 상위 엔티티를 참조하는 관계

    /**
     * 사용자(회원) 엔티티와의 다대일(N:1) 관계
     * - 회원이 평가한 경우 해당 필드 사용
     * - FetchType.LAZY: 지연 로딩 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 외래키(FK) 컬럼명 지정
    private UsersEntity usersEntity;

    /**
     * 임시사용자(비회원) 엔티티와의 다대일(N:1) 관계
     * - 비회원이 평가한 경우 해당 필드 사용
     * - FetchType.LAZY: 지연 로딩 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "temp_id") // 외래키(FK) 컬럼명 지정
    private TempEntity tempEntity;

    /**
     * 약속공유 엔티티와의 다대일(N:1) 관계
     * - 어떤 약속 공유에 대한 평가인지 연결
     * - FetchType.LAZY: 지연 로딩 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_id") // 외래키(FK) 컬럼명 지정
    private ShareEntity shareEntity;

    // ========== 4. Entity -> Dto 변환 메서드 ==========
    /**
     * 엔티티를 DTO로 변환하는 메서드
     * @return EvalDto 평가 데이터 전송 객체
     */
    public EvalDto toDto() {
        return EvalDto.builder()
                .evalId(this.evalId)
                .userId(this.usersEntity.getUserId())
                .tempId(this.tempEntity.getTempId())
                .shareId(this.shareEntity.getShareId())
                .createDate(this.getCreateDate().toString())
                .updateDate(this.getUpdateDate().toString())
                .build();
    }
}