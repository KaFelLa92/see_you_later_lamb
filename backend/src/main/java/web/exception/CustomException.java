package web.exception;

import lombok.Getter;

/**
 * 커스텀 예외 클래스
 * 비즈니스 로직에서 발생하는 예외를 처리하기 위한 클래스
 *
 * 사용 예시:
 * if (lamb == null) {
 *     throw new CustomException(ErrorCode.LAMB_NOT_FOUND);
 * }
 *
 * 또는 커스텀 메시지:
 * throw new CustomException(ErrorCode.LAMB_NOT_FOUND, "ID: " + lambId);
 */
@Getter
public class CustomException extends RuntimeException {

    /**
     * 에러 코드
     * ErrorCode enum의 값
     */
    private final ErrorCode errorCode;

    /**
     * 추가 메시지
     * 기본 에러 메시지 외에 추가로 전달할 정보
     */
    private final String additionalMessage;

    // ========== 생성자 ==========

    /**
     * 에러 코드만으로 예외 생성
     * @param errorCode 에러 코드
     */
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.additionalMessage = null;
    }

    /**
     * 에러 코드와 추가 메시지로 예외 생성
     * @param errorCode 에러 코드
     * @param additionalMessage 추가 메시지
     */
    public CustomException(ErrorCode errorCode, String additionalMessage) {
        super(errorCode.getMessage() + " " + additionalMessage);
        this.errorCode = errorCode;
        this.additionalMessage = additionalMessage;
    }

    /**
     * 에러 코드와 원인 예외로 예외 생성
     * @param errorCode 에러 코드
     * @param cause 원인 예외
     */
    public CustomException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.additionalMessage = null;
    }

    /**
     * 에러 코드, 추가 메시지, 원인 예외로 예외 생성
     * @param errorCode 에러 코드
     * @param additionalMessage 추가 메시지
     * @param cause 원인 예외
     */
    public CustomException(ErrorCode errorCode, String additionalMessage, Throwable cause) {
        super(errorCode.getMessage() + " " + additionalMessage, cause);
        this.errorCode = errorCode;
        this.additionalMessage = additionalMessage;
    }

    // ========== 헬퍼 메서드 ==========

    /**
     * 전체 에러 메시지 반환
     * 기본 메시지 + 추가 메시지 (있는 경우)
     * @return 전체 에러 메시지
     */
    public String getFullMessage() {
        if (additionalMessage != null && !additionalMessage.isEmpty()) {
            return errorCode.getMessage() + " " + additionalMessage;
        }
        return errorCode.getMessage();
    }

    /**
     * HTTP 상태 코드 반환
     * 에러 코드 범위에 따라 적절한 HTTP 상태 코드 반환
     * @return HTTP 상태 코드
     */
    public int getHttpStatusCode() {
        int code = errorCode.getCode();

        // 1000번대: 공통 에러
        if (code >= 1000 && code < 2000) {
            if (code == 1000) return 500;  // Internal Server Error
            if (code == 1001) return 400;  // Bad Request
            if (code == 1002) return 403;  // Forbidden
            if (code == 1003) return 401;  // Unauthorized
        }

        // 2000번대: 비즈니스 로직 에러 (대부분 404 or 400)
        if (code >= 2000 && code < 3000) {
            // NOT_FOUND 계열 (x000, x100, x200, x300)
            if (code % 100 == 0) return 404;  // Not Found
            // 이미 존재하는 경우 (x004)
            if (code % 10 == 4) return 409;   // Conflict
            // 그 외는 Bad Request
            return 400;
        }

        // 3000번대: 파일 관련 에러
        if (code >= 3000 && code < 4000) {
            if (code == 3003) return 404;     // File Not Found
            return 400;                        // Bad Request
        }

        // 기본값
        return 500;  // Internal Server Error
    }
}