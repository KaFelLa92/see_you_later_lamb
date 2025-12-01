package web.model.dto.lamb.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 등장 확률 변경 요청 DTO
 * 양/늑대/희귀등급 등장 확률을 변경할 때 사용하는 요청 데이터
 *
 * Request Body 예시:
 * {
 *   "probLamb": 70,      // 양 등장 확률 70%
 *   "probWolf": 30,      // 늑대 등장 확률 30%
 *   "probRare": 10       // 희귀등급 보정 +10%
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProbUpdateRequest {

    /**
     * 양 등장 확률
     * - 선택 입력 (null이면 기존 값 유지)
     * - 0~100 범위
     */
    private Integer probLamb;

    /**
     * 늑대 등장 확률
     * - 선택 입력 (null이면 기존 값 유지)
     * - 0~100 범위
     */
    private Integer probWolf;

    /**
     * 희귀등급 등장 확률 보정
     * - 선택 입력 (null이면 기존 값 유지)
     * - -100~100 범위 (음수는 확률 감소, 양수는 확률 증가)
     */
    private Integer probRare;

    // ========== 유효성 검증 메서드 ==========

    /**
     * 최소 하나 이상의 필드가 입력되었는지 확인
     * @return 수정할 내용이 있으면 true, 모두 null이면 false
     */
    public boolean hasUpdateData() {
        return probLamb != null || probWolf != null || probRare != null;
    }

    /**
     * 입력된 데이터의 유효성 검증
     * @return 유효하면 true, 아니면 false
     */
    public boolean isValid() {
        // 양 등장 확률이 입력되었다면 0~100 범위여야 함
        if (probLamb != null && (probLamb < 0 || probLamb > 100)) {
            return false;
        }
        // 늑대 등장 확률이 입력되었다면 0~100 범위여야 함
        if (probWolf != null && (probWolf < 0 || probWolf > 100)) {
            return false;
        }
        // 희귀등급 보정이 입력되었다면 -100~100 범위여야 함
        if (probRare != null && (probRare < -100 || probRare > 100)) {
            return false;
        }
        // 양 확률과 늑대 확률이 모두 입력된 경우, 합이 100이어야 함
        if (probLamb != null && probWolf != null && (probLamb + probWolf != 100)) {
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
            return "변경할 확률을 입력해주세요.";
        }
        if (probLamb != null && (probLamb < 0 || probLamb > 100)) {
            return "양 등장 확률은 0~100 사이여야 합니다.";
        }
        if (probWolf != null && (probWolf < 0 || probWolf > 100)) {
            return "늑대 등장 확률은 0~100 사이여야 합니다.";
        }
        if (probRare != null && (probRare < -100 || probRare > 100)) {
            return "희귀등급 보정은 -100~100 사이여야 합니다.";
        }
        if (probLamb != null && probWolf != null && (probLamb + probWolf != 100)) {
            return "양 확률과 늑대 확률의 합은 100이어야 합니다.";
        }
        return "유효성 검증 실패";
    }
}