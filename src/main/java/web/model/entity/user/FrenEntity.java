package web.model.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.user.FrenDto;
import web.model.entity.BaseTime;

/**
 * 친구(Friend) 엔티티
 * 사용자 간의 친구 관계를 관리하는 엔티티 클래스
 */
@Entity
@Table(name = "friend")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FrenEntity extends BaseTime {

    // ========== 1. 테이블 필드 설계 ==========

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private int frenId;        // 친구번호 (PK)

    /**
     * 친구 상태 필드
     * 1 : 현재 친구 (수락됨)
     * 0 : 친구 신청 중 (대기)
     * -1 : 더 이상 친구 아님 (삭제됨)
     */
    @Column(nullable = false) // NOT NULL 제약조건
    private int frenState = 0;     // 친구 상태 (기본값: 0)

    // ========== 2. 양방향 연결 ==========
    // 상위 엔티티가 하위 엔티티를 참조하는 관계
    // (현재 없음)

    // ========== 3. 단방향 연결 ==========
    // 하위 엔티티가 상위 엔티티를 참조하는 관계

    /**
     * 친구 요청을 보낸 사용자(Offer User)와의 다대일(N:1) 관계
     * - FetchType.LAZY: 지연 로딩 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fren_offer", nullable = false) // 외래키 컬럼명, NOT NULL
    private UsersEntity offerUser;

    /**
     * 친구 요청을 받은 사용자(Receiver User)와의 다대일(N:1) 관계
     * - FetchType.LAZY: 지연 로딩 설정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fren_receiver", nullable = false) // 외래키 컬럼명, NOT NULL
    private UsersEntity receiverUser;

    // ========== 4. Entity -> Dto 변환 메서드 ==========
    /**
     * 엔티티를 DTO로 변환하는 메서드
     * @return FrenDto 친구 관계 데이터 전송 객체
     */
    public FrenDto toDto() {
        return FrenDto.builder()
                .frenId(this.frenId)
                .frenState(this.frenState)
                .frenOffer(this.offerUser.getUserId())
                .frenReceiver(this.receiverUser.getUserId())
                .createDate(this.getCreateDate().toString())
                .updateDate(this.getUpdateDate().toString())
                .build();
    }
}