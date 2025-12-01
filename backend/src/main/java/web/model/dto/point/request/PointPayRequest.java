package web.model.dto.point.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 포인트 지급/차감 요청 DTO
 * 사용자에게 포인트를 지급하거나 차감할 때 사용하는 요청 데이터
 *
 * Request Body 예시:
 * {
 *   "userId": 1,
 *   "pointPolicyId": 1,
 *   "atenId": 5,
 *   "reason": "출석 포인트 지급"
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointPayRequest {

    /**
     * 대상 사용자 ID
     * - 필수 입력
     * - 포인트를 받을/차감할 사용자
     */
    private Integer userId;

    /**
     * 포인트 정책 ID
     * - 필수 입력
     * - 어떤 정책에 따라 포인트를 지급/차감할지
     */
    private Integer pointPolicyId;

    /**
     * 출석 ID (선택)
     * - 출석으로 인한 포인트 지급 시
     */
    private Integer atenId;

    /**
     * 약속 공유 ID (선택)
     * - 약속 이행으로 인한 포인트 지급 시
     */
    private Integer shareId;

    /**
     * 목장 업무 ID (선택)
     * - 목장 업무 완료로 인한 포인트 지급 시
     */
    private Integer workId;

    /**
     * 목장 ID (선택)
     * - 목장 구매로 인한 포인트 차감 시
     */
    private Integer farmId;

    /**
     * 포인트 지급/차감 사유
     * - 선택 입력
     * - 로그 목적
     */
    private String reason;

    // ========== 유효성 검증 메서드 ==========

    /**
     * 필수 필드 검증
     * @return 유효하면 true, 아니면 false
     */
    public boolean isValid() {
        // 사용자 ID 검증
        if (userId == null || userId <= 0) {
            return false;
        }
        // 포인트 정책 ID 검증
        if (pointPolicyId == null || pointPolicyId <= 0) {
            return false;
        }
        return true;
    }

    /**
     * 유효성 검증 실패 시 에러 메시지 반환
     * @return 에러 메시지
     */
    public String getValidationErrorMessage() {
        if (userId == null || userId <= 0) {
            return "유효한 사용자를 선택해주세요.";
        }
        if (pointPolicyId == null || pointPolicyId <= 0) {
            return "유효한 포인트 정책을 선택해주세요.";
        }
        return "유효성 검증 실패";
    }

    /**
     * 포인트 지급 활동 타입 확인
     * @return 활동 타입 문자열
     */
    public String getActivityType() {
        if (atenId != null) return "출석";
        if (shareId != null) return "약속 이행";
        if (workId != null) return "목장 업무";
        if (farmId != null) return "목장 구매";
        return "기타";
    }
}