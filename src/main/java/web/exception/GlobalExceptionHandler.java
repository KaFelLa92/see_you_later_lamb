package web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import web.model.dto.common.ApiResponse;

/**
 * 전역 예외 처리 핸들러
 * 모든 Controller에서 발생하는 예외를 중앙에서 처리
 *
 * @RestControllerAdvice: 모든 @RestController에 적용
 * @Slf4j: Lombok 로깅 어노테이션
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ========== 1. CustomException 처리 ==========
    /**
     * 비즈니스 로직에서 발생한 커스텀 예외 처리
     *
     * @param ex CustomException
     * @return ApiResponse 에러 응답
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException ex) {
        // 로그 출력 (에러 코드 + 메시지)
        log.error("CustomException: [{}] {}", ex.getErrorCode().getCode(), ex.getFullMessage());

        // HTTP 상태 코드 결정
        int statusCode = ex.getHttpStatusCode();

        // ApiResponse 생성
        ApiResponse<Void> response = ApiResponse.error(
                ex.getFullMessage(),
                statusCode
        );

        // ResponseEntity로 반환
        return ResponseEntity
                .status(statusCode)
                .body(response);
    }

    // ========== 2. 파일 업로드 크기 초과 예외 처리 ==========
    /**
     * 파일 크기가 설정된 최대 크기를 초과했을 때 발생하는 예외 처리
     *
     * application.properties 설정:
     * spring.servlet.multipart.max-file-size=10MB
     * spring.servlet.multipart.max-request-size=10MB
     *
     * @param ex MaxUploadSizeExceededException
     * @return ApiResponse 에러 응답
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex) {

        log.error("MaxUploadSizeExceededException: {}", ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                "파일 크기가 너무 큽니다. 최대 10MB까지 업로드 가능합니다.",
                400
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // ========== 3. IllegalArgumentException 처리 ==========
    /**
     * 잘못된 인자가 전달되었을 때 발생하는 예외 처리
     *
     * @param ex IllegalArgumentException
     * @return ApiResponse 에러 응답
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        log.error("IllegalArgumentException: {}", ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                "잘못된 요청입니다: " + ex.getMessage(),
                400
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // ========== 4. NullPointerException 처리 ==========
    /**
     * Null 값에 접근했을 때 발생하는 예외 처리
     *
     * @param ex NullPointerException
     * @return ApiResponse 에러 응답
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Void>> handleNullPointerException(
            NullPointerException ex) {

        log.error("NullPointerException: {}", ex.getMessage(), ex);

        ApiResponse<Void> response = ApiResponse.serverError(
                "서버 내부 오류가 발생했습니다."
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    // ========== 5. 그 외 모든 예외 처리 ==========
    /**
     * 위에서 처리하지 못한 모든 예외를 처리
     *
     * @param ex Exception
     * @return ApiResponse 에러 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        // 스택 트레이스 포함하여 로그 출력
        log.error("Unexpected Exception: {}", ex.getMessage(), ex);

        ApiResponse<Void> response = ApiResponse.serverError(
                "서버 내부 오류가 발생했습니다."
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    // ========== 6. 추가: 특정 예외 처리 예시 ==========

    /**
     * 데이터베이스 관련 예외 처리 (옵션)
     *
     * @param ex DataAccessException
     * @return ApiResponse 에러 응답
     */
    /*
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccessException(
            DataAccessException ex) {

        log.error("DataAccessException: {}", ex.getMessage(), ex);

        ApiResponse<Void> response = ApiResponse.serverError(
                "데이터베이스 오류가 발생했습니다."
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
    */
}