package web.model.dto.farm.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 목장 이름 짓기 요청 DTO
 * 사용자가 자신의 목장 이름을 변경할 때 사용하는 요청 데이터
 *
 * Request Body 예시:
 * {
 *   "ownerName": "행복한 우리 목장"
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmNamingRequest {

    /**
     * 새 목장 별명
     * - 필수 입력
     * - 최대 50자
     */
    private String ownerName;

    // ========== 유효성 검증 메서드 ==========

    /**
     * 필수 필드 검증
     * @return 유효하면 true, 아니면 false
     */
    public boolean isValid() {
        // 목장 별명 검증
        if (ownerName == null || ownerName.trim().isEmpty()) {
            return false;
        }
        // 목장 별명 길이 검증 (최대 50자)
        if (ownerName.length() > 50) {
            return false;
        }
        return true;
    }

    /**
     * 유효성 검증 실패 시 에러 메시지 반환
     * @return 에러 메시지
     */
    public String getValidationErrorMessage() {
        if (ownerName == null || ownerName.trim().isEmpty()) {
            return "목장 이름을 입력해주세요.";
        }
        if (ownerName.length() > 50) {
            return "목장 이름은 50자 이하여야 합니다.";
        }
        return "유효성 검증 실패";
    }
}