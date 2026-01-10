package web.model.dto.lamb.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 양 특성 등록 요청 DTO (관리자용)
 * 관리자가 새로운 양 특성을 등록할 때 사용하는 요청 데이터
 *
 * Request Body 예시:
 * {
 *   "charName": "빠른 이동",
 *   "charDesc": "이동 속도가 빠릅니다",
 *   "effectType": "speed",
 *   "effectValue": "1.5",
 *   "isActive": 1
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambCharCreateRequest {

    /**
     * 특성명
     * - 필수 입력
     * - 예: 빠른 이동, 높은 점프, 강한 방어 등
     */
    private String charName;

    /**
     * 특성 설명
     * - 필수 입력
     * - 해당 특성에 대한 상세 설명
     */
    private String charDesc;

    /**
     * 효과 분류
     * - 필수 입력
     * - 예: speed, jump, defense 등
     */
    private String effectType;

    /**
     * 효과 값
     * - 필수 입력
     * - 특성의 구체적인 수치
     */
    private String effectValue;

    /**
     * 활성화 여부
     * - 기본값: 1 (활성화)
     * - 1: 활성화, 0: 비활성화
     */
    @Builder.Default
    private int isActive = 1;

    // ========== 유효성 검증 메서드 ==========

    /**
     * 필수 필드 검증
     * @return 유효하면 true, 아니면 false
     */
    public boolean isValid() {
        // 특성명은 null이 아니고 비어있지 않아야 함
        if (charName == null || charName.trim().isEmpty()) {
            return false;
        }
        // 특성 설명은 null이 아니고 비어있지 않아야 함
        if (charDesc == null || charDesc.trim().isEmpty()) {
            return false;
        }
        // 효과 분류는 null이 아니고 비어있지 않아야 함
        if (effectType == null || effectType.trim().isEmpty()) {
            return false;
        }
        // 효과 값은 null이 아니고 비어있지 않아야 함
        if (effectValue == null || effectValue.trim().isEmpty()) {
            return false;
        }
        // 활성화 여부는 0 또는 1이어야 함
        if (isActive != 0 && isActive != 1) {
            return false;
        }
        return true;
    }

    /**
     * 유효성 검증 실패 시 에러 메시지 반환
     * @return 에러 메시지
     */
    public String getValidationErrorMessage() {
        if (charName == null || charName.trim().isEmpty()) {
            return "특성명은 필수 입력입니다.";
        }
        if (charDesc == null || charDesc.trim().isEmpty()) {
            return "특성 설명은 필수 입력입니다.";
        }
        if (effectType == null || effectType.trim().isEmpty()) {
            return "효과 분류는 필수 입력입니다.";
        }
        if (effectValue == null || effectValue.trim().isEmpty()) {
            return "효과 값은 필수 입력입니다.";
        }
        if (isActive != 0 && isActive != 1) {
            return "활성화 여부는 0 또는 1이어야 합니다.";
        }
        return "유효성 검증 실패";
    }
}