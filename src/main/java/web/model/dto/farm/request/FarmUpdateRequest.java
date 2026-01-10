package web.model.dto.farm.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 목장 수정 요청 DTO (관리자용)
 * 관리자가 기존 목장 유형 정보를 수정할 때 사용하는 요청 데이터
 *
 * Request Body 예시:
 * {
 *   "farmName": "푸른 초원 목장 (업그레이드)",
 *   "farmInfo": "더욱 넓어진 초원",
 *   "maxLamb": 30,
 *   "farmCost": 1500
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmUpdateRequest {

    /**
     * 목장명
     * - 선택 입력 (null이면 기존 값 유지)
     */
    private String farmName;

    /**
     * 목장 소개
     * - 선택 입력 (null이면 기존 값 유지)
     */
    private String farmInfo;

    /**
     * 최대 양 수
     * - 선택 입력 (null이면 기존 값 유지)
     * - null이 아니면 1 이상이어야 함
     */
    private Integer maxLamb;

    /**
     * 목장 구매 비용
     * - 선택 입력 (null이면 기존 값 유지)
     * - null이 아니면 0 이상이어야 함
     */
    private Integer farmCost;

    // ========== 유효성 검증 메서드 ==========

    /**
     * 최소 하나 이상의 필드가 입력되었는지 확인
     * @return 수정할 내용이 있으면 true, 모두 null이면 false
     */
    public boolean hasUpdateData() {
        return farmName != null || farmInfo != null || maxLamb != null || farmCost != null;
    }

    /**
     * 입력된 데이터의 유효성 검증
     * @return 유효하면 true, 아니면 false
     */
    public boolean isValid() {
        // 목장명이 입력되었다면 비어있지 않아야 함
        if (farmName != null && farmName.trim().isEmpty()) {
            return false;
        }
        // 목장 소개가 입력되었다면 비어있지 않아야 함
        if (farmInfo != null && farmInfo.trim().isEmpty()) {
            return false;
        }
        // 최대 양 수가 입력되었다면 1 이상이어야 함
        if (maxLamb != null && maxLamb < 1) {
            return false;
        }
        // 목장 비용이 입력되었다면 0 이상이어야 함
        if (farmCost != null && farmCost < 0) {
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
        if (farmName != null && farmName.trim().isEmpty()) {
            return "목장명은 비어있을 수 없습니다.";
        }
        if (farmInfo != null && farmInfo.trim().isEmpty()) {
            return "목장 소개는 비어있을 수 없습니다.";
        }
        if (maxLamb != null && maxLamb < 1) {
            return "최대 양 수는 1 이상이어야 합니다.";
        }
        if (farmCost != null && farmCost < 0) {
            return "목장 구매 비용은 0 이상이어야 합니다.";
        }
        return "유효성 검증 실패";
    }
}