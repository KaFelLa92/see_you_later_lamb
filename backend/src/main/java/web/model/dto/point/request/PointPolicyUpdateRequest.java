package web.model.dto.point.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 포인트 정책 수정 요청 DTO (관리자용)
 * 관리자가 포인트 정책을 수정할 때 사용하는 요청 데이터
 *
 * Request Body 예시:
 * {
 *   "pointName": "출석 포인트",
 *   "updatePoint": 20
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointPolicyUpdateRequest {

    /**
     * 포인트 정책명
     * - 선택 입력 (null이면 기존 값 유지)
     * - 예: "출석 포인트", "약속 이행 포인트" 등
     */
    private String pointName;

    /**
     * 지급/차감 포인트
     * - 선택 입력 (null이면 기존 값 유지)
     * - 양수: 적립, 음수: 차감
     */
    private Integer updatePoint;

    // ========== 유효성 검증 메서드 ==========

    /**
     * 최소 하나 이상의 필드가 입력되었는지 확인
     * @return 수정할 내용이 있으면 true, 모두 null이면 false
     */
    public boolean hasUpdateData() {
        return pointName != null || updatePoint != null;
    }

    /**
     * 입력된 데이터의 유효성 검증
     * @return 유효하면 true, 아니면 false
     */
    public boolean isValid() {
        // 포인트명이 입력되었다면 비어있지 않아야 함
        if (pointName != null && pointName.trim().isEmpty()) {
            return false;
        }
        // updatePoint는 어떤 값이든 허용 (음수도 가능)
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
        if (pointName != null && pointName.trim().isEmpty()) {
            return "포인트명은 비어있을 수 없습니다.";
        }
        return "유효성 검증 실패";
    }
}