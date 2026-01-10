package web.model.dto.promise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.promise.TempEntity;

/**
 * 임시사용자(Temporary User) DTO
 * 비회원 임시사용자 정보를 전달하기 위한 데이터 전송 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TempDto {

    // ========== 1. 필드 설계 ==========

    private int tempId;                // 임시사용자번호 (PK)
    private String tempName;           // 임시사용자명
    private String createDate;         // 생성일
    private String updateDate;         // 수정일

    // ========== 2. Dto -> Entity 변환 메서드 ==========
    /**
     * DTO를 엔티티로 변환하는 메서드 (생성용)
     * @return TempEntity 임시사용자 엔티티
     */
    public TempEntity toEntity() {
        return TempEntity.builder()
                .tempId(tempId)
                .tempName(tempName)
                .build();
    }
}