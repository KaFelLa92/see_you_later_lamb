package web.model.dto.farm;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.farm.OwnerEntity;
import web.model.entity.farm.WorkEntity;

/**
 * 목장 업무(Farm Work) DTO
 * 목장 업무 정보를 전달하기 위한 데이터 전송 객체
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkDto {

    // ========== 1. 필드 설계 ==========

    private int workId;                    // 목장업무번호 (PK)
    private String workName;               // 목장 업무명

    /**
     * 업무 상태
     * -1: 기한 종료 (실패)
     * 0: 미실행 (대기)
     * 1: 완료 (성공)
     */
    private int workState;                 // 업무 상태

    /**
     * 업무 안내 종료 일시
     * - 업무를 완료했거나 기한이 종료된 시간
     */
    private LocalDateTime workEndDate;    // 업무 안내 종료 일시

    private int ownerId;                   // 목장주번호 (FK)
    private String createDate;             // 생성일
    private String updateDate;             // 수정일

    // ========== 2. Dto -> Entity 변환 메서드 ==========
    /**
     * DTO를 엔티티로 변환하는 메서드 (생성용)
     * @param ownerEntity 목장주 엔티티
     * @return WorkEntity 목장 업무 엔티티
     */
    public WorkEntity toEntity(OwnerEntity ownerEntity) {
        return WorkEntity.builder()
                .workId(workId)
                .workName(workName)
                .workState(workState)
                .workEndDate(workEndDate)
                .ownerEntity(ownerEntity)
                .build();
    }
}