package web.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.user.AtenEntity;
import web.model.entity.user.UsersEntity;

import java.time.LocalDate;

/**
 * 출석(Attendance) DTO
 * 출석 정보를 전달하기 위한 데이터 전송 객체
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AtenDto {

    // ========== 1. 필드 설계 ==========

    private int atenId;            // 출석번호 (PK)
    private LocalDate atenDate;    // 출석일시
    private int userId;            // 사용자번호 (FK)
    private String createDate;     // 생성일
    private String updateDate;     // 수정일

    // ========== 2. Dto -> Entity 변환 메서드 ==========
    /**
     * DTO를 엔티티로 변환하는 메서드 (생성용)
     * @param usersEntity 사용자 엔티티
     * @return AtenEntity 출석 엔티티
     */
    public AtenEntity toEntity(UsersEntity usersEntity) {
        return AtenEntity.builder()
                .atenId(atenId)
                .atenDate(atenDate)
                .usersEntity(usersEntity)
                .build();
    }
}