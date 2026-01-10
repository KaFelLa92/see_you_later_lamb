package web.model.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 통일된 응답 형식 DTO
 * 모든 API 응답을 일관된 형태로 반환하기 위한 클래스
 *
 * 사용 예시:
 * - 성공: ApiResponse.success("조회 성공", data)
 * - 실패: ApiResponse.error("조회 실패")
 *
 * @param <T> 응답 데이터의 타입 (제네릭)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 성공 여부
     * - true: 요청 성공
     * - false: 요청 실패
     */
    private boolean success;

    /**
     * 응답 메시지
     * - 성공/실패에 대한 설명
     * - 예: "양 등록 성공", "양을 찾을 수 없습니다" 등
     */
    private String message;

    /**
     * 응답 데이터
     * - 실제 반환할 데이터 (객체, 리스트 등)
     * - 제네릭 타입으로 다양한 데이터 타입 지원
     */
    private T data;

    /**
     * HTTP 상태 코드
     * - 200: OK
     * - 201: Created
     * - 400: Bad Request
     * - 404: Not Found
     * - 500: Internal Server Error 등
     */
    private int statusCode;

    // ========== 정적 팩토리 메서드 (Static Factory Methods) ==========

    /**
     * 성공 응답 생성 (데이터 포함)
     * @param message 성공 메시지
     * @param data 응답 데이터
     * @param <T> 데이터 타입
     * @return ApiResponse 성공 응답 객체
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .statusCode(200)
                .build();
    }
    /**
     * 성공 응답 생성 (데이터 없음)
     * @param message 성공 메시지
     * @param <T> 데이터 타입
     * @return ApiResponse 성공 응답 객체
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(null)
                .statusCode(200)
                .build();
    }

    /**
     * 생성 성공 응답 (201 Created)
     * @param message 성공 메시지
     * @param data 생성된 데이터
     * @param <T> 데이터 타입
     * @return ApiResponse 생성 성공 응답 객체
     */
    public static <T> ApiResponse<T> created(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .statusCode(201)
                .build();
    }

    /**
     * 실패 응답 생성
     * @param message 실패 메시지
     * @param <T> 데이터 타입
     * @return ApiResponse 실패 응답 객체
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .statusCode(400)
                .build();
    }

    /**
     * 실패 응답 생성 (상태 코드 지정)
     * @param message 실패 메시지
     * @param statusCode HTTP 상태 코드
     * @param <T> 데이터 타입
     * @return ApiResponse 실패 응답 객체
     */
    public static <T> ApiResponse<T> error(String message, int statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .statusCode(statusCode)
                .build();
    }

    /**
     * Not Found 응답 (404)
     * @param message 에러 메시지
     * @param <T> 데이터 타입
     * @return ApiResponse Not Found 응답 객체
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .statusCode(404)
                .build();
    }

    /**
     * 서버 에러 응답 (500)
     * @param message 에러 메시지
     * @param <T> 데이터 타입
     * @return ApiResponse 서버 에러 응답 객체
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .statusCode(500)
                .build();
    }
}