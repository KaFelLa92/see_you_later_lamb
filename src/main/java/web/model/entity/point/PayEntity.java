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

/**
 * 포인트 지급(Point Pay) 엔티티
 * 포인트 적립 공식을 사용자와 연결하는 엔티티 클래스
 * 출석, 약속 이행, 목장 업무, 목장 구매 등 다양한 활동에 대한 포인트 지급 내역 관리
 */
@Entity
@Table(name = "pointPay")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayEntity extends BaseTime {

    // ========== 1. 테이블 필드 설계 ==========

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private int payId;                     // 포인트지급번호 (PK)

    // ========== 2. 양방향 연결 ==========
    // 상위 엔티티가 하위 엔티티를 참조하는 관계
    // (현재 없음)

    // ========== 3. 단방향 연결 ==========
    // 하위 엔티티가 상위 엔티티를 참조하는 관계

    /**
     * 출석(Attendance) 엔티티와의 다대일(N:1) 관계
     * - 출석 시 포인트 지급
     * - nullable = true: 출석으로 인한 지급이 아닐 수 있음
     * - FetchType.LAZY: 지연 로딩 설정
     * - cascade 없음: 출석 삭제 시 포인트 지급 내역은 유지
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true, name = "aten_id") // 외래키(FK) 컬럼명
    private AtenEntity atenEntity;

    /**
     * 약속공유(Share) 엔티티와의 다대일(N:1) 관계
     * - 약속을 잘 수행했을 때 포인트 지급
     * - nullable = true: 약속 공유로 인한 지급이 아닐 수 있음
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true, name = "share_id")
    private ShareEntity shareEntity;

    /**
     * 목장업무(Work) 엔티티와의 다대일(N:1) 관계
     * - 목장 업무 수행 시 포인트 지급
     * - nullable = true: 업무로 인한 지급이 아닐 수 있음
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true, name = "work_id")
    private WorkEntity workEntity;

    /**
     * 목장(Farm) 엔티티와의 다대일(N:1) 관계
     * - 목장 구매 관련 포인트 처리
     * - nullable = true: 목장 구매로 인한 지급이 아닐 수 있음
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true, name = "farm_id")
    private FarmEntity farmEntity;

    /**
     * 포인트정책(Point Policy) 엔티티와의 다대일(N:1) 관계
     * - 어떤 포인트 정책에 따라 지급되었는지 연결
     * - nullable = true: 특정 정책 없이 지급될 수 있음
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true, name = "point_id")
    private PointEntity pointEntity;

    // ========== 4. Entity -> Dto 변환 메서드 ==========
    /**
     * 엔티티를 DTO로 변환하는 메서드
     *
     * 주의: 각 연관 엔티티는 nullable이므로 null 체크 필요
     *
     * @return PayDto 포인트 지급 데이터 전송 객체
     */
    public PayDto toDto() {
        return PayDto.builder()
                .payId(this.payId)
                .atenId(this.atenEntity != null ? this.atenEntity.getAtenId() : 0)
                .shareId(this.shareEntity != null ? this.shareEntity.getShareId() : 0)
                .workId(this.workEntity != null ? this.workEntity.getWorkId() : 0)
                .farmId(this.farmEntity != null ? this.farmEntity.getFarmId() : 0)
                .pointId(this.pointEntity != null ? this.pointEntity.getPointId() : 0)
                .createDate(this.getCreateDate().toString())
                .updateDate(this.getUpdateDate().toString())
                .build();
    }
}