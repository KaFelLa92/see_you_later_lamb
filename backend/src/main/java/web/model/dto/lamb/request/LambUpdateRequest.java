package web.model.dto.lamb.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.common.LambRank;

/**
 * 양 수정 요청 DTO (관리자용)
 * 관리자가 기존 양 품종 정보를 수정할 때 사용하는 요청 데이터
 *
 * Request Body 예시:
 * {
 *   "lambName": "풍성해양 (수정됨)",
 *   "lambInfo": "더욱 풍성한 털을 가진 양입니다",
 *   "lambRank": "RARE",
 *   "charId": 2
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambUpdateRequest {

    /**
     * 양 품종명
     * - 선택 입력 (null이면 기존 값 유지)
     */
    private String lambName;

    /**
     * 양 소개
     * - 선택 입력 (null이면 기존 값 유지)
     */
    private String lambInfo;

    /**
     * 양 등급
     * - 선택 입력 (null이면 기존 값 유지)
     */
    private LambRank lambRank;

    /**
     * 양 특성 번호 (FK)
     * - 선택 입력 (null이면 기존 값 유지)
     * - null 체크를 위해 Integer 타입 사용
     */
    private Integer charId;

    // ========== 유효성 검증 메서드 ==========

    /**
     * 최소 하나 이상의 필드가 입력되었는지 확인
     * @return 수정할 내용이 있으면 true, 모두 null이면 false
     */
    public boolean hasUpdateData() {
        return lambName != null || lambInfo != null || lambRank != null || charId != null;
    }

    /**
     * 입력된 데이터의 유효성 검증
     * @return 유효하면 true, 아니면 false
     */
    public boolean isValid() {
        // 품종명이 입력되었다면 비어있지 않아야 함
        if (lambName != null && lambName.trim().isEmpty()) {
            return false;
        }
        // 소개가 입력되었다면 비어있지 않아야 함
        if (lambInfo != null && lambInfo.trim().isEmpty()) {
            return false;
        }
        // 특성 ID가 입력되었다면 양수여야 함
        if (charId != null && charId <= 0) {
            return false;
        }
        return true;
    }

    /**
     * 유효성 검증 실패 시 에러 메시지 반환
     * @return 에러 메시지
     */
    public String getValidationErrorMessage() {
        if (!hasUpdateData()) {
            return "수정할 내용을 입력해주세요.";
        }
        if (lambName != null && lambName.trim().isEmpty()) {
            return "양 품종명은 비어있을 수 없습니다.";
        }
        if (lambInfo != null && lambInfo.trim().isEmpty()) {
            return "양 소개는 비어있을 수 없습니다.";
        }
        if (charId != null && charId <= 0) {
            return "유효한 양 특성을 선택해주세요.";
        }
        return "유효성 검증 실패";
    }
}