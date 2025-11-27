package web.model.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.user.AtenDto;
import web.model.entity.BaseTime;

import java.time.LocalDate;

/**
 * 출석(Attendance) 엔티티
 * 사용자의 출석 정보를 관리하는 엔티티 클래스
 */
@Entity
@Table(name = "attendance")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AtenEntity extends BaseTime {

    // ========== 1. 테이블 필드 설계 ==========

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private int atenId;                                    // 출석번호 (PK)

    @Column(nullable = false) // NOT NULL 제약조건
    private LocalDate atenDate;                            // 출석일시

    // ========== 2. 양방향 연결 ==========
    // 상위 엔티티가 하위 엔티티를 참조하는 관계
    // (현재 없음)

    // ========== 3. 단방향 연결 ==========
    // 하위 엔티티가 상위 엔티티를 참조하는 관계

    /**
     * 사용자(Users) 엔티티와의 다대일(N:1) 관계
     * - 한 명의 사용자는 여러 출석 기록을 가질 수 있음
     * - FetchType.LAZY: 지연 로딩 설정 (실제 사용 시점에 조회)
     * - cascade 없음: 사용자 삭제 시 출석 기록은 함께 삭제되지 않음
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 외래키(FK) 컬럼명 지정
    private UsersEntity usersEntity;

    // ========== 4. Entity -> Dto 변환 메서드 ==========
    /**
     * 엔티티를 DTO로 변환하는 메서드
     * @return AtenDto 출석 데이터 전송 객체
     */
    public AtenDto toDto() {
        return AtenDto.builder()
                .atenId(this.atenId)
                .atenDate(this.atenDate)
                .userId(this.usersEntity.getUserId())
                .createDate(this.getCreateDate().toString())
                .updateDate(this.getUpdateDate().toString())
                .build();
    }
}