package web.model.dto.farm.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 목장 업무 완료 요청 DTO
 * 사용자가 미니게임을 완료했을 때 사용하는 요청 데이터
 *
 * Request Body 예시:
 * {
 *   "success": true,
 *   "score": 95
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkCompleteRequest {

    /**
     * 미니게임 성공 여부
     * - 필수 입력
     * - true: 성공, false: 실패
     */
    private Boolean success;

    /**
     * 미니게임 점수
     * - 선택 입력
     * - 0~100 범위
     */
    private Integer score;

    // ========== 유효성 검증 메서드 ==========

    /**
     * 필수 필드 검증
     * @return 유효하면 true, 아니면 false
     */
    public boolean isValid() {
        // 성공 여부는 필수
        if (success == null) {
            return false;
        }
        // 점수가 입력된 경우 0~100 범위 검증
        if (score != null && (score < 0 || score > 100)) {
            return false;
        }
        return true;
    }

    /**
     * 유효성 검증 실패 시 에러 메시지 반환
     * @return 에러 메시지
     */
    public String getValidationErrorMessage() {
        if (success == null) {
            return "미니게임 결과를 입력해주세요.";
        }
        if (score != null && (score < 0 || score > 100)) {
            return "점수는 0~100 사이여야 합니다.";
        }
        return "유효성 검증 실패";
    }
}