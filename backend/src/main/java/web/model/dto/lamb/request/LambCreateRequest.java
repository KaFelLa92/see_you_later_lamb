package web.model.dto.lamb.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.common.LambRank;

/**
 * 양 등록 요청 DTO (관리자용)
 * 관리자가 새로운 양 품종을 등록할 때 사용하는 요청 데이터
 *
 * Request Body 예시:
 * {
 *   "lambName": "풍성해양",
 *   "lambInfo": "털이 풍성한 양입니다",
 *   "lambRank": "COMMON",
 *   "charId": 1
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambCreateRequest {

    /**
     * 양 품종명
     * - 필수 입력
     * - 예: 풍성해양, 겁없어양, 배고파양 등
     */
    private String lambName;

    /**
     * 양 소개
     * - 필수 입력
     * - 해당 양 품종에 대한 설명
     */
    private String lambInfo;

    /**
     * 양 등급
     * - 필수 입력
     * - COMMON(일반), RARE(희귀), SPECIAL(특급), LEGENDARY(전설)
     */
    private LambRank lambRank;

    /**
     * 양 특성 번호 (FK)
     * - 필수 입력
     * - LambCharEntity의 charId 참조
     */
    private int charId;

    // ========== 유효성 검증 메서드 ==========

    /**
     * 필수 필드 검증
     * @return 유효하면 true, 아니면 false
     */
    public boolean isValid() {
        // 품종명과 소개는 null이 아니고 비어있지 않아야 함
        if (lambName == null || lambName.trim().isEmpty()) {
            return false;
        }
        if (lambInfo == null || lambInfo.trim().isEmpty()) {
            return false;
        }
        // 등급은 null이 아니어야 함
        if (lambRank == null) {
            return false;
        }
        // 특성 ID는 양수여야 함
        if (charId <= 0) {
            return false;
        }
        return true;
    }

    /**
     * 유효성 검증 실패 시 에러 메시지 반환
     * @return 에러 메시지
     */
    public String getValidationErrorMessage() {
        if (lambName == null || lambName.trim().isEmpty()) {
            return "양 품종명은 필수 입력입니다.";
        }
        if (lambInfo == null || lambInfo.trim().isEmpty()) {
            return "양 소개는 필수 입력입니다.";
        }
        if (lambRank == null) {
            return "양 등급은 필수 입력입니다.";
        }
        if (charId <= 0) {
            return "유효한 양 특성을 선택해주세요.";
        }
        return "유효성 검증 실패";
    }
}