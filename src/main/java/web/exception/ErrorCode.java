package web.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에러 코드 Enum
 * 시스템에서 발생하는 모든 에러에 대한 코드와 메시지를 정의
 *
 * 사용 예시:
 * throw new CustomException(ErrorCode.LAMB_NOT_FOUND);
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ========== 공통 에러 (1000번대) ==========
    /**
     * 일반적인 서버 내부 에러
     */
    INTERNAL_SERVER_ERROR(1000, "서버 내부 오류가 발생했습니다."),

    /**
     * 잘못된 요청 파라미터
     */
    INVALID_INPUT_VALUE(1001, "입력값이 올바르지 않습니다."),

    /**
     * 권한 없음
     */
    FORBIDDEN(1002, "권한이 없습니다."),

    /**
     * 인증 실패
     */
    UNAUTHORIZED(1003, "인증이 필요합니다."),

    // ========== 양(Lamb) 관련 에러 (2000번대) ==========
    /**
     * 양을 찾을 수 없음
     */
    LAMB_NOT_FOUND(2000, "양을 찾을 수 없습니다."),

    /**
     * 양 등록 실패
     */
    LAMB_CREATE_FAILED(2001, "양 등록에 실패했습니다."),

    /**
     * 양 수정 실패
     */
    LAMB_UPDATE_FAILED(2002, "양 수정에 실패했습니다."),

    /**
     * 양 삭제 실패
     */
    LAMB_DELETE_FAILED(2003, "양 삭제에 실패했습니다."),

    /**
     * 이미 존재하는 양 품종
     */
    LAMB_ALREADY_EXISTS(2004, "이미 존재하는 양 품종입니다."),

    // ========== 양 특성(LambChar) 관련 에러 (2100번대) ==========
    /**
     * 양 특성을 찾을 수 없음
     */
    LAMB_CHAR_NOT_FOUND(2100, "양 특성을 찾을 수 없습니다."),

    /**
     * 양 특성 등록 실패
     */
    LAMB_CHAR_CREATE_FAILED(2101, "양 특성 등록에 실패했습니다."),

    /**
     * 양 특성 수정 실패
     */
    LAMB_CHAR_UPDATE_FAILED(2102, "양 특성 수정에 실패했습니다."),

    /**
     * 양 특성 삭제 실패
     */
    LAMB_CHAR_DELETE_FAILED(2103, "양 특성 삭제에 실패했습니다."),

    // ========== 양치기(Shepherd) 관련 에러 (2200번대) ==========
    /**
     * 양치기를 찾을 수 없음
     */
    SHEP_NOT_FOUND(2200, "양을 찾을 수 없습니다."),

    /**
     * 사용자의 양이 아님
     */
    SHEP_NOT_OWNED(2201, "자신의 양만 관리할 수 있습니다."),

    /**
     * 양 이름 짓기 실패
     */
    SHEP_NAMING_FAILED(2202, "양 이름 짓기에 실패했습니다."),

    /**
     * 양 밥 주기 실패
     */
    SHEP_FEEDING_FAILED(2203, "양 밥 주기에 실패했습니다."),

    /**
     * 양 털 깎기 실패
     */
    SHEP_SHAVING_FAILED(2204, "양 털 깎기에 실패했습니다."),

    /**
     * 양 장소 옮기기 실패
     */
    SHEP_MOVING_FAILED(2205, "양 장소 옮기기에 실패했습니다."),

    /**
     * 이미 배부른 양
     */
    SHEP_ALREADY_FED(2206, "이미 배부른 양입니다."),

    /**
     * 털이 없는 양
     */
    SHEP_NO_FUR(2207, "이미 털이 없는 양입니다."),

    /**
     * 늑대에게 쫓기는 양
     */
    SHEP_CHASED_BY_WOLF(2208, "늑대에게 쫓기는 양은 관리할 수 없습니다."),

    /**
     * 양 등장 실패
     */
    SHEP_APPEAR_FAILED(2209, "양 등장에 실패했습니다."),

    /**
     * 양 실종 실패
     */
    SHEP_MISSING_FAILED(2210, "양 실종 처리에 실패했습니다."),

    // ========== 확률(Probability) 관련 에러 (2300번대) ==========
    /**
     * 확률 정보를 찾을 수 없음
     */
    PROB_NOT_FOUND(2300, "확률 정보를 찾을 수 없습니다."),

    /**
     * 확률 변경 실패
     */
    PROB_UPDATE_FAILED(2301, "확률 변경에 실패했습니다."),

    /**
     * 잘못된 확률 값
     */
    INVALID_PROBABILITY(2302, "확률 값은 0~100 사이여야 합니다."),

    // ========== 파일 업로드 관련 에러 (3000번대) ==========
    /**
     * 파일 업로드 실패
     */
    FILE_UPLOAD_FAILED(3000, "파일 업로드에 실패했습니다."),

    /**
     * 지원하지 않는 파일 형식
     */
    INVALID_FILE_TYPE(3001, "지원하지 않는 파일 형식입니다."),

    /**
     * 파일 크기 초과
     */
    FILE_SIZE_EXCEEDED(3002, "파일 크기가 너무 큽니다."),

    /**
     * 파일을 찾을 수 없음
     */
    FILE_NOT_FOUND(3003, "파일을 찾을 수 없습니다."),

    // ========== 목장(Farm) 관련 에러 (4000번대) ==========
    /**
     * 목장을 찾을 수 없음
     */
    FARM_NOT_FOUND(4000, "목장을 찾을 수 없습니다."),

    /**
     * 목장 등록 실패
     */
    FARM_CREATE_FAILED(4001, "목장 등록에 실패했습니다."),

    /**
     * 목장 수정 실패
     */
    FARM_UPDATE_FAILED(4002, "목장 수정에 실패했습니다."),

    /**
     * 목장 삭제 실패
     */
    FARM_DELETE_FAILED(4003, "목장 삭제에 실패했습니다."),

    /**
     * 이미 존재하는 목장명
     */
    FARM_ALREADY_EXISTS(4004, "이미 존재하는 목장명입니다."),

    // ========== 목장주(Owner) 관련 에러 (4100번대) ==========
    /**
     * 목장주를 찾을 수 없음
     */
    OWNER_NOT_FOUND(4100, "목장을 찾을 수 없습니다."),

    /**
     * 사용자의 목장이 아님
     */
    OWNER_NOT_OWNED(4101, "자신의 목장만 관리할 수 있습니다."),

    /**
     * 목장 구매 실패
     */
    FARM_BUY_FAILED(4102, "목장 구매에 실패했습니다."),

    /**
     * 이미 구매한 목장
     */
    FARM_ALREADY_OWNED(4103, "이미 이 목장을 보유하고 있습니다."),

    /**
     * 포인트 부족
     */
    INSUFFICIENT_POINTS(4104, "포인트가 부족합니다."),

    /**
     * 목장 이름 짓기 실패
     */
    FARM_NAMING_FAILED(4105, "목장 이름 짓기에 실패했습니다."),

    // ========== 목장 업무(Work) 관련 에러 (4200번대) ==========
    /**
     * 업무를 찾을 수 없음
     */
    WORK_NOT_FOUND(4200, "업무를 찾을 수 없습니다."),

    /**
     * 업무 처리 실패
     */
    WORK_PROCESS_FAILED(4201, "업무 처리에 실패했습니다."),

    /**
     * 이미 완료된 업무
     */
    WORK_ALREADY_COMPLETED(4202, "이미 완료된 업무입니다."),

    /**
     * 기한이 지난 업무
     */
    WORK_EXPIRED(4203, "기한이 지난 업무입니다."),

    // ========== 포인트(Point) 관련 에러 (5000번대) ==========
    /**
     * 포인트 정책을 찾을 수 없음
     */
    POINT_POLICY_NOT_FOUND(5000, "포인트 정책을 찾을 수 없습니다."),

    /**
     * 포인트 정책 수정 실패
     */
    POINT_POLICY_UPDATE_FAILED(5001, "포인트 정책 수정에 실패했습니다."),

    /**
     * 포인트 정책 삭제 실패
     */
    POINT_POLICY_DELETE_FAILED(5002, "포인트 정책 삭제에 실패했습니다."),

    /**
     * 포인트 지급 실패
     */
    POINT_PAY_FAILED(5003, "포인트 지급에 실패했습니다."),

    /**
     * 이미 지급된 포인트
     */
    POINT_ALREADY_PAID(5005, "이미 포인트가 지급되었습니다.");

    // ========== 필드 ==========
    /**
     * 에러 코드 (숫자)
     */
    private final int code;

    /**
     * 에러 메시지
     */
    private final String message;
}