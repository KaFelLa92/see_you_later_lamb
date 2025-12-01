package web.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.model.dto.common.ApiResponse;
import web.model.dto.common.PageResponse;
import web.model.dto.point.*;
import web.model.dto.point.request.*;
import web.model.entity.common.UserRole;
import web.service.PointService;

/**
 * 포인트(Point) 관련 API 컨트롤러
 * - 포인트 정책 관리 (관리자)
 * - 포인트 정책 조회 (공통)
 * - 포인트 지급/차감 (공통)
 */
@Slf4j
@RestController
@RequestMapping("/api/point")
@RequiredArgsConstructor
public class PointController {

    // ========== DI (Dependency Injection) ==========
    /**
     * 포인트 관련 비즈니스 로직을 처리하는 서비스
     */
    private final PointService pointService;

    // ========== 헬퍼 메서드 ==========

    /**
     * HttpServletRequest에서 사용자 ID 추출
     * JwtInterceptor에서 request에 저장한 userId를 가져옴
     *
     * @param request HttpServletRequest
     * @return Integer 사용자 ID
     */
    private Integer getUserIdFromRequest(HttpServletRequest request) {
        return (Integer) request.getAttribute("userId");
    }

    /**
     * HttpServletRequest에서 사용자 권한 추출
     *
     * @param request HttpServletRequest
     * @return String 사용자 권한 (ROLE_USER, ROLE_ADMIN)
     */
    private String getRoleFromRequest(HttpServletRequest request) {
        return (String) request.getAttribute("role");
    }

    /**
     * 관리자 권한 확인
     *
     * @param request HttpServletRequest
     * @return boolean 관리자이면 true
     */
    private boolean isAdmin(HttpServletRequest request) {
        String role = getRoleFromRequest(request);
        return UserRole.ROLE_ADMIN.name().equals(role);
    }

    /**
     * 페이징 파라미터로 Pageable 객체 생성
     *
     * @param page 페이지 번호 (0부터 시작, 기본값 0)
     * @param size 페이지 크기 (기본값 10)
     * @param sortBy 정렬 기준 필드 (기본값 "createDate")
     * @param direction 정렬 방향 (ASC/DESC, 기본값 DESC)
     * @return Pageable 페이징 객체
     */
    private Pageable createPageable(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "ASC".equalsIgnoreCase(direction)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(sortDirection, sortBy);
        return PageRequest.of(page, size, sort);
    }

    // ========================================================
    // [관리자] 포인트 정책 관리 (AP-01, AP-02)
    // ========================================================

    /**
     * AP-01: 포인트 정책 수정 (관리자)
     * PUT /api/point/policy/{pointId}
     *
     * Request Body:
     * {
     *   "pointName": "출석 포인트",
     *   "updatePoint": 20
     * }
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "포인트 정책 수정 성공",
     *   "data": { PointDto },
     *   "statusCode": 200
     * }
     */
    @PutMapping("/policy/{pointId}")
    public ResponseEntity<ApiResponse<PointDto>> updatePointPolicy(
            HttpServletRequest servletRequest,
            @PathVariable int pointId,
            @RequestBody PointPolicyUpdateRequest request) {

        log.info("[포인트 정책 수정 요청] pointId: {}", pointId);

        // 1. 관리자 권한 확인
        if (!isAdmin(servletRequest)) {
            log.warn("[포인트 정책 수정 실패] 관리자 권한 필요");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("관리자만 포인트 정책을 수정할 수 있습니다.", 403));
        }

        // 2. 포인트 정책 수정
        PointDto result = pointService.updatePointPolicy(pointId, request);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("포인트 정책 수정 성공", result));
    }

    /**
     * AP-02: 포인트 정책 삭제 (관리자)
     * DELETE /api/point/policy/{pointId}
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "포인트 정책 삭제 성공",
     *   "statusCode": 200
     * }
     */
    @DeleteMapping("/policy/{pointId}")
    public ResponseEntity<ApiResponse<Void>> deletePointPolicy(
            HttpServletRequest servletRequest,
            @PathVariable int pointId) {

        log.info("[포인트 정책 삭제 요청] pointId: {}", pointId);

        // 1. 관리자 권한 확인
        if (!isAdmin(servletRequest)) {
            log.warn("[포인트 정책 삭제 실패] 관리자 권한 필요");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("관리자만 포인트 정책을 삭제할 수 있습니다.", 403));
        }

        // 2. 포인트 정책 삭제
        pointService.deletePointPolicy(pointId);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("포인트 정책 삭제 성공"));
    }

    // ========================================================
    // [공통] 포인트 정책 조회 (AP-03, AP-04)
    // ========================================================

    /**
     * AP-03: 포인트 정책 전체 조회
     * GET /api/point/policy?page=0&size=10&sortBy=createDate&direction=DESC
     *
     * Query Parameters:
     * - page: 페이지 번호 (0부터 시작, 기본값 0)
     * - size: 페이지 크기 (기본값 10)
     * - sortBy: 정렬 기준 필드 (기본값 "createDate")
     * - direction: 정렬 방향 (ASC/DESC, 기본값 DESC)
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "포인트 정책 전체 조회 성공",
     *   "data": {
     *     "content": [ { PointDto }, ... ],
     *     "currentPage": 0,
     *     "pageSize": 10,
     *     "totalElements": 10,
     *     "totalPages": 1,
     *     "first": true,
     *     "last": true,
     *     "empty": false
     *   },
     *   "statusCode": 200
     * }
     */
    @GetMapping("/policy")
    public ResponseEntity<ApiResponse<PageResponse<PointDto>>> getAllPointPolicies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        log.info("[포인트 정책 전체 조회 요청] page: {}, size: {}", page, size);

        // 1. Pageable 객체 생성
        Pageable pageable = createPageable(page, size, sortBy, direction);

        // 2. 포인트 정책 전체 조회
        PageResponse<PointDto> result = pointService.getAllPointPolicies(pageable);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("포인트 정책 전체 조회 성공", result));
    }

    /**
     * AP-04: 포인트 정책 상세 조회
     * GET /api/point/policy/{pointId}
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "포인트 정책 상세 조회 성공",
     *   "data": { PointDto },
     *   "statusCode": 200
     * }
     */
    @GetMapping("/policy/{pointId}")
    public ResponseEntity<ApiResponse<PointDto>> getDetailPointPolicy(
            @PathVariable int pointId) {

        log.info("[포인트 정책 상세 조회 요청] pointId: {}", pointId);

        // 1. 포인트 정책 상세 조회
        PointDto result = pointService.getDetailPointPolicy(pointId);

        // 2. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("포인트 정책 상세 조회 성공", result));
    }

    // ========================================================
    // [공통] 포인트 지급/차감 (AP-05)
    // ========================================================

    /**
     * AP-05: 포인트 지급/차감
     * POST /api/point/pay
     *
     * Request Body:
     * {
     *   "userId": 1,
     *   "pointPolicyId": 1,
     *   "atenId": 5,
     *   "reason": "출석 포인트 지급"
     * }
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "포인트 지급 성공",
     *   "data": { PayDto },
     *   "statusCode": 201
     * }
     */
    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<PayDto>> createPoint(
            @RequestBody PointPayRequest request) {

        log.info("[포인트 지급/차감 요청] userId: {}, policyId: {}, 활동: {}",
                request.getUserId(),
                request.getPointPolicyId(),
                request.getActivityType());

        // 1. 포인트 지급/차감
        PayDto result = pointService.createPoint(request);

        // 2. 성공 응답
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("포인트 지급/차감 성공", result));
    }
}