package web.model.entity.promise;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.dto.promise.TempDto;
import web.model.entity.BaseTime;

/**
 * 임시사용자(Temporary User) 엔티티
 * 비회원이 약속을 평가할 때 사용하는 임시 사용자 정보 관리 엔티티 클래스
 */
@Entity
@Table(name = "temp")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempEntity extends BaseTime {

    // ========== 1. 테이블 필드 설계 ==========

    @Id // 기본키(Primary Key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Increment 설정
    private int tempId;                        // 임시사용자번호 (PK)

    /**
     * 임시사용자명 필드
     * - 비회원 사용자의 이름
     * - 기본값: "지나가던 양치기"
     */
    @Column(nullable = false, length = 30) // NOT NULL, 최대 30자
    @Builder.Default
    private String tempName = "지나가던 양치기";  // 임시사용자명 (기본값)

    // ========== 2. 양방향 연결 ==========
    // 상위 엔티티가 하위 엔티티를 참조하는 관계
    // (현재 없음)

    // ========== 3. 단방향 연결 ==========
    // 하위 엔티티가 상위 엔티티를 참조하는 관계
    // (현재 없음)

    // ========== 4. Entity -> Dto 변환 메서드 ==========
    /**
     * 엔티티를 DTO로 변환하는 메서드
     * @return TempDto 임시사용자 데이터 전송 객체
     */
    public TempDto toDto() {
        return TempDto.builder()
                .tempId(this.tempId)
                .tempName(this.tempName)
                .createDate(this.getCreateDate().toString())
                .updateDate(this.getUpdateDate().toString())
                .build();
    }
}