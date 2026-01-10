package web.model.dto.lamb.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 양 액션 요청 DTO
 * 사용자가 양에게 행동을 취할 때 사용하는 요청 데이터
 * - 양 이름 짓기
 * - 양 밥 주기
 * - 양 털 깎기
 * - 양 장소 옮기기
 *
 * Request Body 예시:
 * {
 *   "shepName": "복실이"        // 이름 짓기 시
 * }
 *
 * {
 *   "shepExist": 1              // 장소 옮기기 시 (1: 울타리, 0: 목장)
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShepActionRequest {

    /**
     * 양 별명
     * - 양 이름 짓기 시 사용
     * - 최대 30자
     */
    private String shepName;

    /**
     * 양 존재 위치
     * - 양 장소 옮기기 시 사용
     * - 1: 울타리로 이동
     * - 0: 목장으로 이동
     */
    private Integer shepExist;

    // ========== 유효성 검증 메서드 ==========

    /**
     * 이름 짓기 요청 유효성 검증
     * @return 유효하면 true, 아니면 false
     */
    public boolean isValidForNaming() {
        // 이름은 null이 아니고 비어있지 않아야 함
        if (shepName == null || shepName.trim().isEmpty()) {
            return false;
        }
        // 이름은 30자 이하여야 함
        if (shepName.length() > 30) {
            return false;
        }
        return true;
    }

    /**
     * 장소 옮기기 요청 유효성 검증
     * @return 유효하면 true, 아니면 false
     */
    public boolean isValidForMoving() {
        // shepExist는 null이 아니어야 함
        if (shepExist == null) {
            return false;
        }
        // shepExist는 0 또는 1이어야 함
        if (shepExist != 0 && shepExist != 1) {
            return false;
        }
        return true;
    }

    /**
     * 이름 짓기 검증 실패 시 에러 메시지 반환
     * @return 에러 메시지
     */
    public String getNamingValidationError() {
        if (shepName == null || shepName.trim().isEmpty()) {
            return "양 이름을 입력해주세요.";
        }
        if (shepName.length() > 30) {
            return "양 이름은 30자 이하여야 합니다.";
        }
        return "유효성 검증 실패";
    }

    /**
     * 장소 옮기기 검증 실패 시 에러 메시지 반환
     * @return 에러 메시지
     */
    public String getMovingValidationError() {
        if (shepExist == null) {
            return "이동할 장소를 선택해주세요.";
        }
        if (shepExist != 0 && shepExist != 1) {
            return "유효하지 않은 장소입니다. (0: 목장, 1: 울타리)";
        }
        return "유효성 검증 실패";
    }
}