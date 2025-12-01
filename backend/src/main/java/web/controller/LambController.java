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
import org.springframework.web.multipart.MultipartFile;
import web.model.dto.common.ApiResponse;
import web.model.dto.common.PageResponse;
import web.model.dto.lamb.*;
import web.model.dto.lamb.request.*;
import web.model.entity.common.UserRole;
import web.service.LambService;

/**
 * 양(Lamb) 관련 API 컨트롤러
 * - 양 품종 관리 (관리자)
 * - 양 특성 관리 (관리자)
 * - 양 조회 (공통)
 * - 양 관리 (사용자)
 * - 확률 관리 (관리자/사용자)
 */
@Slf4j
@RestController
@RequestMapping("/api/lamb")
@RequiredArgsConstructor
public class LambController {

    // ========== DI (Dependency Injection) ==========
    /**
     * 양 관련 비즈니스 로직을 처리하는 서비스
     */
    private final LambService lambService;

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
        // 정렬 방향 결정
        Sort.Direction sortDirection = "ASC".equalsIgnoreCase(direction)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        // 정렬 객체 생성
        Sort sort = Sort.by(sortDirection, sortBy);

        // Pageable 객체 생성 (페이지 번호, 페이지 크기, 정렬)
        return PageRequest.of(page, size, sort);
    }

    // ========================================================
    // [관리자] 양 품종 관리 (AL-01, AL-02, AL-03)
    // ========================================================

    /**
     * AL-01: 양 등록 (관리자)
     * POST /api/lamb
     * <p>
     * Request:
     * - Content-Type: multipart/form-data
     * - request: JSON 형태의 양 등록 요청 데이터
     * - imageFile: 양 일러스트 이미지 파일 (선택)
     * <p>
     * Response:
     * {
     * "success": true,
     * "message": "양 등록 성공",
     * "data": { LambDto },
     * "statusCode": 201
     * }
     */
    @PostMapping
    public ResponseEntity<ApiResponse<LambDto>> createLamb(
            HttpServletRequest servletRequest,
            @RequestPart("request") LambCreateRequest request,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        log.info("[양 등록 요청] 품종명: {}, 등급: {}", request.getLambName(), request.getLambRank());

        // 1. 관리자 권한 확인
        if (!isAdmin(servletRequest)) {
            log.warn("[양 등록 실패] 관리자 권한 필요");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("관리자만 양을 등록할 수 있습니다.", 403));
        }

        // 2. 양 등록
        LambDto result = lambService.createLamb(request, imageFile);

        // 3. 성공 응답
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("양 등록 성공", result));
    }

    /**
     * AL-02: 양 수정 (관리자)
     * PUT /api/lamb/{lambId}
     * <p>
     * Request:
     * - Content-Type: multipart/form-data
     * - request: JSON 형태의 양 수정 요청 데이터
     * - imageFile: 새 일러스트 이미지 파일 (선택)
     * <p>
     * Response:
     * {
     * "success": true,
     * "message": "양 수정 성공",
     * "data": { LambDto },
     * "statusCode": 200
     * }
     */
    @PutMapping("/{lambId}")
    public ResponseEntity<ApiResponse<LambDto>> updateLamb(
            HttpServletRequest servletRequest,
            @PathVariable int lambId,
            @RequestPart("request") LambUpdateRequest request,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        log.info("[양 수정 요청] lambId: {}", lambId);

        // 1. 관리자 권한 확인
        if (!isAdmin(servletRequest)) {
            log.warn("[양 수정 실패] 관리자 권한 필요");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("관리자만 양을 수정할 수 있습니다.", 403));
        }

        // 2. 양 수정
        LambDto result = lambService.updateLamb(lambId, request, imageFile);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("양 수정 성공", result));
    }

    /**
     * AL-03: 양 삭제 (관리자)
     * DELETE /api/lamb/{lambId}
     * <p>
     * Response:
     * {
     * "success": true,
     * "message": "양 삭제 성공",
     * "statusCode": 200
     * }
     */
    @DeleteMapping("/{lambId}")
    public ResponseEntity<ApiResponse<Void>> deleteLamb(
            HttpServletRequest servletRequest,
            @PathVariable int lambId) {

        log.info("[양 삭제 요청] lambId: {}", lambId);

        // 1. 관리자 권한 확인
        if (!isAdmin(servletRequest)) {
            log.warn("[양 삭제 실패] 관리자 권한 필요");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("관리자만 양을 삭제할 수 있습니다.", 403));
        }

        // 2. 양 삭제
        lambService.deleteLamb(lambId);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("양 삭제 성공"));
    }

    // ========================================================
    // [관리자] 양 특성 관리 (AL-04, AL-05, AL-06)
    // ========================================================

    /**
     * AL-04: 양 특성 등록 (관리자)
     * POST /api/lamb/char
     *
     * Request Body:
     * {
     *   "charName": "빠른 이동",
     *   "charDesc": "이동 속도가 빠릅니다",
     *   "effectType": "speed",
     *   "effectValue": "1.5",
     *   "isActive": 1
     * }
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "양 특성 등록 성공",
     *   "data": { LambCharDto },
     *   "statusCode": 201
     * }
     */
    @PostMapping("/char")
    public ResponseEntity<ApiResponse<LambCharDto>> createLambChar(
            HttpServletRequest servletRequest,
            @RequestBody LambCharCreateRequest request) {

        log.info("[양 특성 등록 요청] 특성명: {}", request.getCharName());

        // 1. 관리자 권한 확인
        if (!isAdmin(servletRequest)) {
            log.warn("[양 특성 등록 실패] 관리자 권한 필요");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("관리자만 양 특성을 등록할 수 있습니다.", 403));
        }

        // 2. 양 특성 등록
        LambCharDto result = lambService.createLambChar(request);

        // 3. 성공 응답
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("양 특성 등록 성공", result));
    }

    /**
     * AL-05: 양 특성 수정 (관리자)
     * PUT /api/lamb/char/{charId}
     *
     * Request Body:
     * {
     *   "charName": "빠른 이동 (수정)",
     *   "charDesc": "더욱 빠른 이동 속도",
     *   "effectType": "speed",
     *   "effectValue": "2.0",
     *   "isActive": 1
     * }
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "양 특성 수정 성공",
     *   "data": { LambCharDto },
     *   "statusCode": 200
     * }
     */
    @PutMapping("/char/{charId}")
    public ResponseEntity<ApiResponse<LambCharDto>> updateLambChar(
            HttpServletRequest servletRequest,
            @PathVariable int charId,
            @RequestBody LambCharCreateRequest request) {

        log.info("[양 특성 수정 요청] charId: {}", charId);

        // 1. 관리자 권한 확인
        if (!isAdmin(servletRequest)) {
            log.warn("[양 특성 수정 실패] 관리자 권한 필요");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("관리자만 양 특성을 수정할 수 있습니다.", 403));
        }

        // 2. 양 특성 수정
        LambCharDto result = lambService.updateLambChar(charId, request);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("양 특성 수정 성공", result));
    }

    /**
     * AL-06: 양 특성 삭제 (관리자)
     * DELETE /api/lamb/char/{charId}
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "양 특성 삭제 성공",
     *   "statusCode": 200
     * }
     */
    @DeleteMapping("/char/{charId}")
    public ResponseEntity<ApiResponse<Void>> deleteLambChar(
            HttpServletRequest servletRequest,
            @PathVariable int charId) {

        log.info("[양 특성 삭제 요청] charId: {}", charId);

        // 1. 관리자 권한 확인
        if (!isAdmin(servletRequest)) {
            log.warn("[양 특성 삭제 실패] 관리자 권한 필요");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("관리자만 양 특성을 삭제할 수 있습니다.", 403));
        }

        // 2. 양 특성 삭제
        lambService.deleteLambChar(charId);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("양 특성 삭제 성공"));
    }

    // ========================================================
    // [공통] 양/양 특성 조회 (LA-01, LA-02, LA-03, LA-04)
    // ========================================================

    /**
     * LA-01: 양 전체 조회
     * GET /api/lamb?page=0&size=10&sortBy=createDate&direction=DESC
     *
     * Query Parameters:
     * - page: 페이지 번호 (0부터 시작, 기본값 0)
     * - size: 페이지 크기 (기본값 10)
     * - sortBy: 정렬 기준 필드 (기본값 "createDate")
     * - direction: 정렬 방향 (ASC/DESC, 기본값 DESC)
     *
     * 권한별 동작:
     * - 관리자: 모든 양 품종 조회
     * - 일반 사용자: 자신이 보유한 양만 조회
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "양 전체 조회 성공",
     *   "data": {
     *     "content": [ { LambDto }, ... ],
     *     "currentPage": 0,
     *     "pageSize": 10,
     *     "totalElements": 50,
     *     "totalPages": 5,
     *     "first": true,
     *     "last": false,
     *     "empty": false
     *   },
     *   "statusCode": 200
     * }
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<LambDto>>> getAllLambs(
            HttpServletRequest servletRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        log.info("[양 전체 조회 요청] page: {}, size: {}, sortBy: {}, direction: {}",
                page, size, sortBy, direction);

        // 1. 사용자 정보 가져오기
        Integer userId = getUserIdFromRequest(servletRequest);
        boolean admin = isAdmin(servletRequest);

        // 2. Pageable 객체 생성
        Pageable pageable = createPageable(page, size, sortBy, direction);

        // 3. 양 전체 조회
        PageResponse<LambDto> result = lambService.getAllLambs(userId, admin, pageable);

        // 4. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("양 전체 조회 성공", result));
    }

    /**
     * LA-02: 양 상세 조회
     * GET /api/lamb/{lambId}
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "양 상세 조회 성공",
     *   "data": { LambDto },
     *   "statusCode": 200
     * }
     */
    @GetMapping("/{lambId}")
    public ResponseEntity<ApiResponse<LambDto>> getDetailLamb(
            HttpServletRequest servletRequest,
            @PathVariable int lambId) {

        log.info("[양 상세 조회 요청] lambId: {}", lambId);

        // 1. 사용자 정보 가져오기
        Integer userId = getUserIdFromRequest(servletRequest);
        boolean admin = isAdmin(servletRequest);

        // 2. 양 상세 조회
        LambDto result = lambService.getDetailLamb(lambId, userId, admin);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("양 상세 조회 성공", result));
    }

    /**
     * LA-03: 양 특성 전체 조회
     * GET /api/lamb/char?page=0&size=10&sortBy=createDate&direction=DESC
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "양 특성 전체 조회 성공",
     *   "data": {
     *     "content": [ { LambCharDto }, ... ],
     *     "currentPage": 0,
     *     "pageSize": 10,
     *     "totalElements": 20,
     *     "totalPages": 2,
     *     "first": true,
     *     "last": false,
     *     "empty": false
     *   },
     *   "statusCode": 200
     * }
     */
    @GetMapping("/char")
    public ResponseEntity<ApiResponse<PageResponse<LambCharDto>>> getAllLambChars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        log.info("[양 특성 전체 조회 요청] page: {}, size: {}", page, size);

        // 1. Pageable 객체 생성
        Pageable pageable = createPageable(page, size, sortBy, direction);

        // 2. 양 특성 전체 조회
        PageResponse<LambCharDto> result = lambService.getAllLambChars(pageable);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("양 특성 전체 조회 성공", result));
    }

    /**
     * LA-04: 양 특성 상세 조회
     * GET /api/lamb/char/{charId}
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "양 특성 상세 조회 성공",
     *   "data": { LambCharDto },
     *   "statusCode": 200
     * }
     */
    @GetMapping("/char/{charId}")
    public ResponseEntity<ApiResponse<LambCharDto>> getDetailLambChar(
            @PathVariable int charId) {

        log.info("[양 특성 상세 조회 요청] charId: {}", charId);

        // 1. 양 특성 상세 조회
        LambCharDto result = lambService.getDetailLambChar(charId);

        // 2. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("양 특성 상세 조회 성공", result));
    }

    // ========================================================
    // [사용자] 양 관리 (LA-05, LA-06, LA-07, LA-08)
    // ========================================================

    /**
     * LA-05: 양 이름 짓기
     * POST /api/lamb/shepherd/{shepId}/naming
     *
     * Request Body:
     * {
     *   "shepName": "복실이"
     * }
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "양 이름 짓기 성공",
     *   "data": { ShepDto },
     *   "statusCode": 200
     * }
     */
    @PostMapping("/shepherd/{shepId}/naming")
    public ResponseEntity<ApiResponse<ShepDto>> namingLamb(
            HttpServletRequest servletRequest,
            @PathVariable int shepId,
            @RequestBody ShepActionRequest request) {

        log.info("[양 이름 짓기 요청] shepId: {}, 새 이름: {}", shepId, request.getShepName());

        // 1. 사용자 ID 가져오기
        Integer userId = getUserIdFromRequest(servletRequest);

        // 2. 양 이름 짓기
        ShepDto result = lambService.namingLamb(shepId, userId, request);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("양 이름 짓기 성공", result));
    }

    /**
     * LA-06: 양 밥 주기
     * POST /api/lamb/shepherd/{shepId}/feeding
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "양 밥 주기 성공",
     *   "data": { ShepDto },
     *   "statusCode": 200
     * }
     */
    @PostMapping("/shepherd/{shepId}/feeding")
    public ResponseEntity<ApiResponse<ShepDto>> feedingLamb(
            HttpServletRequest servletRequest,
            @PathVariable int shepId) {

        log.info("[양 밥 주기 요청] shepId: {}", shepId);

        // 1. 사용자 ID 가져오기
        Integer userId = getUserIdFromRequest(servletRequest);

        // 2. 양 밥 주기
        ShepDto result = lambService.feedingLamb(shepId, userId);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("양 밥 주기 성공", result));
    }

    /**
     * LA-07: 양 털 깎기
     * POST /api/lamb/shepherd/{shepId}/shaving
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "양 털 깎기 성공",
     *   "data": { ShepDto },
     *   "statusCode": 200
     * }
     */
    @PostMapping("/shepherd/{shepId}/shaving")
    public ResponseEntity<ApiResponse<ShepDto>> shavingLamb(
            HttpServletRequest servletRequest,
            @PathVariable int shepId) {

        log.info("[양 털 깎기 요청] shepId: {}", shepId);

        // 1. 사용자 ID 가져오기
        Integer userId = getUserIdFromRequest(servletRequest);

        // 2. 양 털 깎기
        ShepDto result = lambService.shavingLamb(shepId, userId);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("양 털 깎기 성공", result));
    }

    /**
     * LA-08: 양 장소 옮기기
     * POST /api/lamb/shepherd/{shepId}/moving
     *
     * Request Body:
     * {
     *   "shepExist": 1  // 1: 울타리로, 0: 목장으로
     * }
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "양 장소 옮기기 성공",
     *   "data": { ShepDto },
     *   "statusCode": 200
     * }
     */
    @PostMapping("/shepherd/{shepId}/moving")
    public ResponseEntity<ApiResponse<ShepDto>> movingLamb(
            HttpServletRequest servletRequest,
            @PathVariable int shepId,
            @RequestBody ShepActionRequest request) {

        log.info("[양 장소 옮기기 요청] shepId: {}, 이동 위치: {}", shepId, request.getShepExist());

        // 1. 사용자 ID 가져오기
        Integer userId = getUserIdFromRequest(servletRequest);

        // 2. 양 장소 옮기기
        ShepDto result = lambService.movingLamb(shepId, userId, request);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("양 장소 옮기기 성공", result));
    }

    // ========================================================
    // [사용자] 양 등장/실종 (LA-09, LA-10)
    // ========================================================

    /**
     * LA-09: 양 등장
     * POST /api/lamb/appear?shareId=123
     *
     * Query Parameters:
     * - shareId: 약속 공유 ID (필수)
     *
     * Response (양 등장):
     * {
     *   "success": true,
     *   "message": "양이 등장했습니다!",
     *   "data": { ShepDto },
     *   "statusCode": 200
     * }
     *
     * Response (늑대 등장):
     * {
     *   "success": false,
     *   "message": "늑대가 등장했습니다.",
     *   "data": null,
     *   "statusCode": 200
     * }
     */
    @PostMapping("/appear")
    public ResponseEntity<ApiResponse<ShepDto>> appearLamb(
            HttpServletRequest servletRequest,
            @RequestParam int shareId) {

        log.info("[양 등장 요청] shareId: {}", shareId);

        // 1. 사용자 ID 가져오기
        Integer userId = getUserIdFromRequest(servletRequest);

        // 2. 양 등장
        ShepDto result = lambService.appearLamb(userId, shareId);

        // 3. 결과에 따라 다른 응답 반환
        if (result != null) {
            // 양 등장
            return ResponseEntity.ok(ApiResponse.success("양이 등장했습니다!", result));
        } else {
            // 늑대 등장
            return ResponseEntity.ok(
                    ApiResponse.<ShepDto>builder()
                            .success(false)
                            .message("늑대가 등장했습니다.")
                            .data(null)
                            .statusCode(200)
                            .build()
            );
        }
    }

    /**
     * LA-10: 양 실종
     * DELETE /api/lamb/shepherd/{shepId}
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "양 실종 처리 성공",
     *   "statusCode": 200
     * }
     */
    @DeleteMapping("/shepherd/{shepId}")
    public ResponseEntity<ApiResponse<Void>> missingLamb(
            HttpServletRequest servletRequest,
            @PathVariable int shepId) {

        log.info("[양 실종 요청] shepId: {}", shepId);

        // 1. 사용자 ID 가져오기
        Integer userId = getUserIdFromRequest(servletRequest);

        // 2. 양 실종 처리
        lambService.missingLamb(shepId, userId);

        // 3. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("양 실종 처리 성공"));
    }

    // ========================================================
    // [확률 관리] (LA-11, LA-12, LA-13)
    // ========================================================

    /**
     * LA-11, LA-12, LA-13: 등장 확률 변경
     * PUT /api/lamb/probability?shareId=123
     *
     * Query Parameters:
     * - shareId: 약속 공유 ID (필수)
     *
     * Request Body:
     * {
     *   "probLamb": 70,    // 양 등장 확률 (선택)
     *   "probWolf": 30,    // 늑대 등장 확률 (선택)
     *   "probRare": 10     // 희귀 등급 보정 (선택)
     * }
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "등장 확률 변경 성공",
     *   "data": { ProbDto },
     *   "statusCode": 200
     * }
     */
    @PutMapping("/probability")
    public ResponseEntity<ApiResponse<ProbDto>> updateProb(
            @RequestParam int shareId,
            @RequestBody ProbUpdateRequest request) {

        log.info("[확률 변경 요청] shareId: {}", shareId);

        // 1. 확률 변경
        ProbDto result = lambService.updateProb(shareId, request);

        // 2. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("등장 확률 변경 성공", result));
    }

    /**
     * 확률 정보 조회
     * GET /api/lamb/probability?shareId=123
     *
     * Query Parameters:
     * - shareId: 약속 공유 ID (필수)
     *
     * Response:
     * {
     *   "success": true,
     *   "message": "확률 조회 성공",
     *   "data": { ProbDto },
     *   "statusCode": 200
     * }
     */
    @GetMapping("/probability")
    public ResponseEntity<ApiResponse<ProbDto>> getProb(
            @RequestParam int shareId) {

        log.info("[확률 조회 요청] shareId: {}", shareId);

        // 1. 확률 조회
        ProbDto result = lambService.getProb(shareId);

        // 2. 성공 응답
        return ResponseEntity.ok(ApiResponse.success("확률 조회 성공", result));
    }

}

