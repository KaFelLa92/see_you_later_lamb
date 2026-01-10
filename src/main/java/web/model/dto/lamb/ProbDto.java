package web.model.dto.lamb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.lamb.ProbEntity;
import web.model.entity.promise.ShareEntity;

/**
 * 등장 확률(Probability) DTO
 * 양과 늑대의 등장 확률 정보를 전달하기 위한 데이터 전송 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProbDto {

    // ========== 1. 필드 설계 ==========

    private int probId;                    // 등장확률번호 (PK)
    private int probLamb;                  // 양 등장 확률
    private int probWolf;                  // 늑대 등장 확률

    /**
     * 희귀 등급 등장 확률
     * - 일반 양을 제외한 희귀, 특급, 전설 등급의 등장 확률
     * - 해당 레코드만큼 확률 보정이 들어감
     */
    private int probRare;                  // 희귀 등급 등장 확률

    /**
     * 약속공유번호 (FK)
     * - 약속 확인과 약속 점수 기반으로 확률을 매김
     */
    private int shareId;                   // 약속공유번호 (FK)

    private String createDate;             // 생성일
    private String updateDate;             // 수정일

    // ========== 2. Dto -> Entity 변환 메서드 ==========
    /**
     * DTO를 엔티티로 변환하는 메서드 (생성용)
     * @param shareEntity 약속공유 엔티티
     * @return ProbEntity 등장 확률 엔티티
     */
    public ProbEntity toEntity(ShareEntity shareEntity) {
        return ProbEntity.builder()
                .probId(probId)
                .probLamb(probLamb)
                .probWolf(probWolf)
                .probRare(probRare)
                .shareEntity(shareEntity)
                .build();
    }
}