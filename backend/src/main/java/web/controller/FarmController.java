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
import web.model.dto.farm.*;
import web.model.dto.farm.request.*;
import web.model.entity.common.UserRole;
import web.service.FarmService;

/**
 * 목장(Farm) 관련 API 컨트롤러
 * - 목장 유형 관리 (관리자)
 * - 목장 조회 (공통)
 * - 업무 조회 (공통)
 * - 목장 구매 및 관리 (사용자)
 * - 목장 업무 처리 (사용자)
 */
@Slf4j
@RestController
@RequestMapping("/api/farm")
@RequiredArgsConstructor
public class FarmController {

    // ========== DI (Dependency Injection) ==========
    /**
     * 목장 관련 비즈니스 로직을 처리하는 서비스
     */
    private final FarmService farmService;

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
     * @param page      페이지 번호 (0부터 시작, 기본값 0)
     * @param size      페이지 크기 (기본값 10)
     * @param sortBy    정렬 기준 필드 (기본값 "createDate")
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
    // [관리자] 목장 유형 관리 (AF-01, AF-02, AF-03)
    // ========================================================

    /**
     * AF-01: 목장 등록 (관리자)
     * POST /api/farm
     * <p>
     * Request Body:
     * {
     * "farmName": "푸른 초원 목장",
     * "farmInfo": "넓은 초원이 펼쳐진 목장입니다",
     * "maxLamb": 20,
     * "farmCost": 1000
     * }
     * <p>
     * Response:
     * {
     * "success": true,
     * "message": "목장 등록 성공",
     * "data": { FarmDto },
     * "statusCode": 201
     * }
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FarmDto>> createFarm(
            HttpServletRequest servletRequest,
            @RequestBody FarmCreateRequest request) {

        log.info("[목장 등록 요청] 목장명: {}", request.getFarmName());

        // 1. 관리자 권한 확인
        if (!isAdmin(servletRequest)) {
            log.warn("[목장 등록 실패] 관리자 권한 필요");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("관리자만 목장을 등록할 수 있습니다.", 403));
        }

        // 2. 목장 등록
        FarmDto result = farmService.createFarm(request);

        // 3. 성공 응답
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("목장 등록 성공", result));
    }

    /**
     * AF-02: 목장 수정 (관리자)
     * PUT /api/farm/{farmId}
     * <p>
     * Request Body:
     * {
     * "farmName": "푸른 초원 목장 (업그레이드)",
     * "farmInfo": "더욱 넓어진 초원",
     * "maxLamb": 30,
     * "farmCost": 1500
     * }
     * <p>
     * Response:
     * {
     * "success": true,
     * "message": "목장 수정 성공",
     * "data": { FarmDto },
     * "statusCode": 200
     * }
     */
    @PutMapping("/{farmId}")
    public ResponseEntity<ApiResponse<FarmDto>> updateFarm(
            HttpServletRequest servletRequest,
            @PathVariable int farmId,
            @RequestBody FarmUpdateRequest request) {

        log.info("[목장 수정 요청] farmId: {}", farmId);

        // 1. 관리자 권한 확인
        if (!isAdmin(servletRequest)) {
            log.warn("[목장 수정 실패] 관리자 권한 필요");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("관리자만 목장을 수정할 수 있습니다.", 403));
        }

        // 2. 목장 수정
        FarmDto result = farmService.updateFarm(farmId, request);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("목장 수정 성공", result));
    }

    /**
     * AF-03: 목장 삭제 (관리자)
     * DELETE /api/farm/{farmId}
     * <p>
     * Response:
     * {
     * "success": true,
     * "message": "목장 삭제 성공",
     * "statusCode": 200
     * }
     */
    @DeleteMapping("/{farmId}")
    public ResponseEntity<ApiResponse<Void>> deleteFarm(
            HttpServletRequest servletRequest,
            @PathVariable int farmId) {

        log.info("[목장 삭제 요청] farmId: {}", farmId);

        // 1. 관리자 권한 확인
        if (!isAdmin(servletRequest)) {
            log.warn("[목장 삭제 실패] 관리자 권한 필요");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("관리자만 목장을 삭제할 수 있습니다.", 403));
        }

        // 2. 목장 삭제
        farmService.deleteFarm(farmId);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("목장 삭제 성공"));
    }

    // ========================================================
    // [공통] 목장 조회 (FA-01, FA-02)
    // ========================================================

    /**
     * FA-01: 목장 전체 조회
     * GET /api/farm?page=0&size=10&sortBy=createDate&direction=DESC
     * <p>
     * Query Parameters:
     * - page: 페이지 번호 (0부터 시작, 기본값 0)
     * - size: 페이지 크기 (기본값 10)
     * - sortBy: 정렬 기준 필드 (기본값 "createDate")
     * - direction: 정렬 방향 (ASC/DESC, 기본값 DESC)
     * <p>
     * 권한별 동작:
     * - 관리자: 모든 목장 유형 조회
     * - 일반 사용자: 자신이 구매한 목장만 조회
     * <p>
     * Response:
     * {
     * "success": true,
     * "message": "목장 전체 조회 성공",
     * "data": {
     * "content": [ { FarmDto }, ... ],
     * "currentPage": 0,
     * "pageSize": 10,
     * "totalElements": 50,
     * "totalPages": 5,
     * "first": true,
     * "last": false,
     * "empty": false
     * },
     * "statusCode": 200
     * }
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<FarmDto>>> getAllFarms(
            HttpServletRequest servletRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        log.info("[목장 전체 조회 요청] page: {}, size: {}", page, size);

        // 1. 사용자 정보 가져오기
        Integer userId = getUserIdFromRequest(servletRequest);
        boolean admin = isAdmin(servletRequest);

        // 2. Pageable 객체 생성
        Pageable pageable = createPageable(page, size, sortBy, direction);

        // 3. 목장 전체 조회
        PageResponse<FarmDto> result = farmService.getAllFarms(userId, admin, pageable);

        // 4. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("목장 전체 조회 성공", result));
    }

    /**
     * FA-02: 목장 상세 조회
     * GET /api/farm/{farmId}
     * <p>
     * Response:
     * {
     * "success": true,
     * "message": "목장 상세 조회 성공",
     * "data": { FarmDto },
     * "statusCode": 200
     * }
     */
    @GetMapping("/{farmId}")
    public ResponseEntity<ApiResponse<FarmDto>> getDetailFarm(
            HttpServletRequest servletRequest,
            @PathVariable int farmId) {

        log.info("[목장 상세 조회 요청] farmId: {}", farmId);

        // 1. 사용자 정보 가져오기
        Integer userId = getUserIdFromRequest(servletRequest);
        boolean admin = isAdmin(servletRequest);

        // 2. 목장 상세 조회
        FarmDto result = farmService.getDetailFarm(farmId, userId, admin);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("목장 상세 조회 성공", result));
    }

    // ========================================================
    // [공통] 업무 조회 (FA-03, FA-04)
    // ========================================================

    /**
     * FA-03: 업무 전체 조회
     * GET /api/farm/work?page=0&size=10&sortBy=createDate&direction=DESC
     * <p>
     * Response:
     * {
     * "success": true,
     * "message": "업무 전체 조회 성공",
     * "data": {
     * "content": [ { WorkDto }, ... ],
     * ...
     * },
     * "statusCode": 200
     * }
     */
    @GetMapping("/work")
    public ResponseEntity<ApiResponse<PageResponse<WorkDto>>> getAllWorks(
            HttpServletRequest servletRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        log.info("[업무 전체 조회 요청] page: {}, size: {}", page, size);

        // 1. 사용자 정보 가져오기
        Integer userId = getUserIdFromRequest(servletRequest);
        boolean admin = isAdmin(servletRequest);

        // 2. Pageable 객체 생성
        Pageable pageable = createPageable(page, size, sortBy, direction);

        // 3. 업무 전체 조회
        PageResponse<WorkDto> result = farmService.getAllWorks(userId, admin, pageable);

        // 4. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("업무 전체 조회 성공", result));
    }

    /**
     * FA-04: 업무 상세 조회
     * GET /api/farm/work/{workId}
     * <p>
     * Response:
     * {
     * "success": true,
     * "message": "업무 상세 조회 성공",
     * "data": { WorkDto },
     * "statusCode": 200
     * }
     */
    @GetMapping("/work/{workId}")
    public ResponseEntity<ApiResponse<WorkDto>> getDetailWork(
            HttpServletRequest servletRequest,
            @PathVariable int workId) {

        log.info("[업무 상세 조회 요청] workId: {}", workId);

        // 1. 사용자 정보 가져오기
        Integer userId = getUserIdFromRequest(servletRequest);
        boolean admin = isAdmin(servletRequest);

        // 2. 업무 상세 조회
        WorkDto result = farmService.getDetailWork(workId, userId, admin);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("업무 상세 조회 성공", result));
    }

    // ========================================================
    // [사용자] 목장 구매 및 관리 (FA-05, FA-06)
    // ========================================================

    /**
     * FA-05: 목장 구매
     * POST /api/farm/buy
     * <p>
     * Request Body:
     * {
     * "farmId": 1,
     * "ownerName": "나의 행복한 목장"
     * }
     * <p>
     * Response:
     * {
     * "success": true,
     * "message": "목장 구매 성공",
     * "data": { OwnerDto },
     * "statusCode": 201
     * }
     */
    @PostMapping("/buy")
    public ResponseEntity<ApiResponse<OwnerDto>> buyFarm(
            HttpServletRequest servletRequest,
            @RequestBody FarmBuyRequest request) {

        log.info("[목장 구매 요청] farmId: {}, ownerName: {}",
                request.getFarmId(), request.getOwnerName());

        // 1. 사용자 ID 가져오기
        Integer userId = getUserIdFromRequest(servletRequest);

        // 2. 목장 구매
        OwnerDto result = farmService.buyFarm(userId, request);

        // 3. 성공 응답
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("목장 구매 성공", result));
    }

    /**
     * FA-06: 목장 이름 짓기
     * POST /api/farm/owner/{ownerId}/naming
     * <p>
     * Request Body:
     * {
     * "ownerName": "행복한 우리 목장"
     * }
     * <p>
     * Response:
     * {
     * "success": true,
     * "message": "목장 이름 짓기 성공",
     * "data": { OwnerDto },
     * "statusCode": 200
     * }
     */
    @PostMapping("/owner/{ownerId}/naming")
    public ResponseEntity<ApiResponse<OwnerDto>> namingFarm(
            HttpServletRequest servletRequest,
            @PathVariable int ownerId,
            @RequestBody FarmNamingRequest request) {

        log.info("[목장 이름 짓기 요청] ownerId: {}, 새 이름: {}",
                ownerId, request.getOwnerName());

        // 1. 사용자 ID 가져오기
        Integer userId = getUserIdFromRequest(servletRequest);

        // 2. 목장 이름 짓기
        OwnerDto result = farmService.namingFarm(ownerId, userId, request);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("목장 이름 짓기 성공", result));
    }

    // ========================================================
    // [사용자] 목장 업무 처리 (FA-07)
    // ========================================================

    /**
     * FA-07: 목장 업무 처리
     * POST /api/farm/work/{workId}/complete
     * <p>
     * Request Body:
     * {
     * "success": true,
     * "score": 95
     * }
     * <p>
     * Response:
     * {
     * "success": true,
     * "message": "업무 처리 성공",
     * "data": { WorkDto },
     * "statusCode": 200
     * }
     */
    @PostMapping("/work/{workId}/complete")
    public ResponseEntity<ApiResponse<WorkDto>> workingFarm(
            HttpServletRequest servletRequest,
            @PathVariable int workId,
            @RequestBody WorkCompleteRequest request) {

        log.info("[목장 업무 처리 요청] workId: {}, success: {}",
                workId, request.getSuccess());

        // 1. 사용자 ID 가져오기
        Integer userId = getUserIdFromRequest(servletRequest);

        // 2. 업무 처리
        WorkDto result = farmService.workingFarm(workId, userId, request);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("업무 처리 성공", result));
    }

    // ========================================================
    // [관리자] 만료 업무 자동 처리 (배치용)
    // ========================================================

    /**
     * 기한이 지난 업무 자동 처리
     * POST /api/farm/work/process-expired
     * <p>
     * 스케줄러나 관리자가 수동으로 호출
     * <p>
     * Response:
     * {
     * "success": true,
     * "message": "만료 업무 5개 처리 완료",
     * "data": 5,
     * "statusCode": 200
     * }
     */
    @PostMapping("/work/process-expired")
    public ResponseEntity<ApiResponse<Integer>> processExpiredWorks(
            HttpServletRequest servletRequest) {

        log.info("[만료 업무 자동 처리 요청]");

        // 1. 관리자 권한 확인
        if (!isAdmin(servletRequest)) {
            log.warn("[만료 업무 처리 실패] 관리자 권한 필요");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("관리자만 실행할 수 있습니다.", 403));
        }

        // 2. 만료 업무 처리
        int count = farmService.processExpiredWorks();

        // 3. 성공 응답
        return ResponseEntity.ok(
                ApiResponse.success("만료 업무 " + count + "개 처리 완료", count)
        );
    }
}