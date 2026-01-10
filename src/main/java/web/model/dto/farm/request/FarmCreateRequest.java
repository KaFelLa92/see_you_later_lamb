package web.model.dto.farm.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 목장 등록 요청 DTO (관리자용)
 * 관리자가 새로운 목장 유형을 등록할 때 사용하는 요청 데이터
 *
 * Request Body 예시:
 * {
 *   "farmName": "푸른 초원 목장",
 *   "farmInfo": "넓은 초원이 펼쳐진 목장입니다",
 *   "maxLamb": 20,
 *   "farmCost": 1000
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmCreateRequest {

    /**
     * 목장명
     * - 필수 입력
     * - 예: 푸른 초원 목장, 산골 목장 등
     */
    private String farmName;

    /**
     * 목장 소개
     * - 필수 입력
     * - 해당 목장 유형에 대한 설명
     */
    private String farmInfo;

    /**
     * 최대 양 수
     * - 필수 입력
     * - 이 목장에서 기를 수 있는 최대 양의 수
     * - 최소 1마리 이상
     */
    private Integer maxLamb;

    /**
     * 목장 구매 비용
     * - 필수 입력
     * - 포인트로 결제
     * - 0 이상
     */
    private Integer farmCost;

    // ========== 유효성 검증 메서드 ==========

    /**
     * 필수 필드 검증
     * @return 유효하면 true, 아니면 false
     */
    public boolean isValid() {
        // 목장명 검증
        if (farmName == null || farmName.trim().isEmpty()) {
            return false;
        }
        // 목장 소개 검증
        if (farmInfo == null || farmInfo.trim().isEmpty()) {
            return false;
        }
        // 최대 양 수 검증 (1 이상)
        if (maxLamb == null || maxLamb < 1) {
            return false;
        }
        // 목장 비용 검증 (0 이상)
        if (farmCost == null || farmCost < 0) {
            return false;
        }
        return true;
    }

    /**
     * 유효성 검증 실패 시 에러 메시지 반환
     * @return 에러 메시지
     */
    public String getValidationErrorMessage() {
        if (farmName == null || farmName.trim().isEmpty()) {
            return "목장명은 필수 입력입니다.";
        }
        if (farmInfo == null || farmInfo.trim().isEmpty()) {
            return "목장 소개는 필수 입력입니다.";
        }
        if (maxLamb == null || maxLamb < 1) {
            return "최대 양 수는 1 이상이어야 합니다.";
        }
        if (farmCost == null || farmCost < 0) {
            return "목장 구매 비용은 0 이상이어야 합니다.";
        }
        return "유효성 검증 실패";
    }
}