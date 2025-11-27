package web.model.dto.promise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.common.CycleType;
import web.model.entity.promise.CalendEntity;
import web.model.entity.promise.PromEntity;

import java.time.LocalDateTime;

/**
 * 캘린더(Calendar) DTO
 * 반복 약속 일정 정보를 전달하기 위한 데이터 전송 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendDto {

    // ========== 1. 필드 설계 ==========

    private int calendId;              // 캘린더번호 (PK)
    private CycleType calendCycle;     // 반복 주기
    private LocalDateTime calendStart; // 반복 시작일
    private LocalDateTime calendEnd;   // 반복 종료일
    private int promId;                // 약속번호 (FK)
    private String createDate;         // 생성일
    private String updateDate;         // 수정일

    // ========== 2. Dto -> Entity 변환 메서드 ==========
    /**
     * DTO를 엔티티로 변환하는 메서드 (생성용)
     * @param promEntity 약속 엔티티
     * @return CalendEntity 캘린더 엔티티
     */
    public CalendEntity toEntity(PromEntity promEntity) {
        return CalendEntity.builder()
                .calendId(calendId)
                .calendCycle(calendCycle)
                .calendStart(calendStart)
                .calendEnd(calendEnd)
                .promEntity(promEntity)
                .build();
    }
}