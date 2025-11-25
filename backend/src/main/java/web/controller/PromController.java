package web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import web.model.dto.promise.CalendDto;
import web.model.dto.promise.PromDto;
import web.model.dto.promise.PromEvaluationDto;
import web.service.PromService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 약속 관련 API 컨트롤러
 * - 약속 생성/수정/삭제/조회
 * - 약속 공유 및 평가
 * - 반복 약속 관리
 */
@RestController
@RequestMapping("/api/promise")
@RequiredArgsConstructor
public class PromController {

    // ============================================
    // [*] DI (Dependency Injection)
    // ============================================
    private final PromService promService;

    /**
     * HttpServletRequest에서 사용자 ID 추출 헬퍼 메서드
     * JwtInterceptor에서 request에 저장한 userId를 가져옴
     */
    private Integer getUserIdFromRequest(HttpServletRequest request) {
        return (Integer) request.getAttribute("userId");
    }

    // ============================================
    // [1] 약속 생성 (PM-01)
    // ============================================

    /**
     * PM-01 약속 생성
     * POST /api/promise
     *
     * Request Body:
     * {
     *   "prom_title": "친구 만남",
     *   "prom_date": "2024-12-25T14:00:00",
     *   "prom_addr": "서울시 강남구 테헤란로",
     *   "prom_addr_detail": "스타벅스 강남점",
     *   "prom_text": "오랜만에 만나서 수다 떨기",
     *   "prom_alert": 30
     * }
     *
     * Response:
     * {
     *   "success": true,
     *   "promise": { ... },
     *   "routeInfo": {
     *     "distance": 5.2,
     *     "estimatedTime": 25,
     *     "method": "대중교통"
     *   }
     * }
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPromise(
            HttpServletRequest request,
            @RequestBody PromDto promDto) {

        Integer userId = getUserIdFromRequest(request);
        Map<String, Object> result = promService.createProm(promDto, userId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // ============================================
    // [2] 약속 수정 (PM-02)
    // ============================================

    /**
     * PM-02 약속 수정
     * PUT /api/promise/{promId}
     *
     * Request Body:
     * {
     *   "prom_title": "수정된 제목",
     *   "prom_date": "2024-12-26T15:00:00"
     * }
     */
    @PutMapping("/{promId}")
    public ResponseEntity<Map<String, Object>> updatePromise(
            HttpServletRequest request,
            @PathVariable int promId,
            @RequestBody PromDto promDto) {

        Integer userId = getUserIdFromRequest(request);
        promDto.setProm_id(promId);

        Map<String, Object> result = promService.updateProm(promDto, userId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // ============================================
    // [3] 약속 메모 추가 (PM-03)
    // ============================================

    /**
     * PM-03 약속 메모 추가
     * POST /api/promise/{promId}/memo
     *
     * Request Body:
     * {
     *   "memo": "늦지 말고 가기!"
     * }
     */
    @PostMapping("/{promId}/memo")
    public ResponseEntity<Map<String, Object>> addMemo(
            HttpServletRequest request,
            @PathVariable int promId,
            @RequestBody Map<String, String> memoData) {

        Integer userId = getUserIdFromRequest(request);
        String memo = memoData.get("memo");

        Map<String, Object> result = promService.memoProm(promId, memo, userId);

        return ResponseEntity.ok(result);
    }

    // ============================================
    // [4] 약속 취소 (PM-04)
    // ============================================

    /**
     * PM-04 약속 취소 (삭제)
     * DELETE /api/promise/{promId}
     */
    @DeleteMapping("/{promId}")
    public ResponseEntity<Map<String, Object>> deletePromise(
            HttpServletRequest request,
            @PathVariable int promId) {

        Integer userId = getUserIdFromRequest(request);
        Map<String, Object> result = promService.deleteProm(promId, userId);

        return ResponseEntity.ok(result);
    }

    // ============================================
    // [5] 약속 전체조회 (PM-05)
    // ============================================

    /**
     * PM-05 약속 전체조회
     * GET /api/promise
     *
     * Query Parameters (옵션):
     * - startDate: 조회 시작 날짜 (예: 2024-12-01T00:00:00)
     * - endDate: 조회 종료 날짜 (예: 2024-12-31T23:59:59)
     *
     * Example:
     * GET /api/promise?startDate=2024-12-01T00:00:00&endDate=2024-12-31T23:59:59
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPromises(
            HttpServletRequest request,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        Integer userId = getUserIdFromRequest(request);
        Map<String, Object> result = promService.getProm(userId, startDate, endDate);

        return ResponseEntity.ok(result);
    }

    // ============================================
    // [6] 약속 상세조회 (PM-06)
    // ============================================

    /**
     * PM-06 약속 상세조회
     * GET /api/promise/{promId}
     */
    @GetMapping("/{promId}")
    public ResponseEntity<Map<String, Object>> getPromiseDetail(
            HttpServletRequest request,
            @PathVariable int promId) {

        Integer userId = getUserIdFromRequest(request);
        Map<String, Object> result = promService.getDetailProm(promId, userId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // ============================================
    // [7] 약속 공유 (PM-07)
    // ============================================

    /**
     * PM-07 약속 공유
     * POST /api/promise/{promId}/share
     *
     * Response:
     * {
     *   "success": true,
     *   "share": { ... },
     *   "shareUrl": "https://yourdomain.com/promise/share/abc123",
     *   "kakaoShareInfo": {
     *     "title": "친구 만남",
     *     "link": "https://...",
     *     ...
     *   }
     * }
     */
    @PostMapping("/{promId}/share")
    public ResponseEntity<Map<String, Object>> sharePromise(
            HttpServletRequest request,
            @PathVariable int promId) {

        Integer userId = getUserIdFromRequest(request);
        Map<String, Object> result = promService.shareProm(promId, userId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // ============================================
    // [8] 약속 평가 (PM-08)
    // ============================================

    /**
     * PM-08 약속 평가 - 회원
     * POST /api/promise/share/{shareId}/eval
     *
     * Request Body:
     * {
     *   "user_id": 1,
     *   "share_check": 1,
     *   "share_score": 5,
     *   "share_feedback": "시간 잘 지켜줘서 고마워요!"
     * }
     */
    @PostMapping("/share/{shareId}/eval")
    public ResponseEntity<Map<String, Object>> evaluatePromise(
            @PathVariable int shareId,
            @RequestBody PromEvaluationDto evalDto) {

        Map<String, Object> result = promService.evalProm(evalDto, shareId, false);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * PM-08 약속 평가 - 임시 사용자
     * POST /api/promise/share/{shareId}/eval/temp
     *
     * Request Body:
     * {
     *   "temp_name": "익명의 양치기",
     *   "share_check": 1,
     *   "share_score": 5,
     *   "share_feedback": "좋았어요!"
     * }
     */
    @PostMapping("/share/{shareId}/eval/temp")
    public ResponseEntity<Map<String, Object>> evaluatePromiseByTemp(
            @PathVariable int shareId,
            @RequestBody PromEvaluationDto evalDto) {

        Map<String, Object> result = promService.evalProm(evalDto, shareId, true);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // ============================================
    // [9] 공유 링크로 약속 조회 (공개 API - 인증 불필요)
    // ============================================

    /**
     * 공유 토큰으로 약속 조회 (비회원 접근 가능)
     * GET /api/promise/share/{shareToken}
     *
     * 이 API는 WebConfig에서 인증 제외 필요:
     * .excludePathPatterns("/api/promise/share/**")
     */
    @GetMapping("/share/{shareToken}")
    public ResponseEntity<Map<String, Object>> getPromiseByShare(@PathVariable String shareToken) {
        Map<String, Object> result = promService.getPromByShareToken(shareToken);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ============================================
    // [10] 반복 약속 등록 (PM-09)
    // ============================================

    /**
     * PM-09 반복 약속 등록
     * POST /api/promise/{promId}/cycle
     *
     * Request Body:
     * {
     *   "calend_cycle": "WEEKLY",
     *   "calend_start": "2024-12-01T00:00:00",
     *   "calend_end": "2025-12-31T23:59:59"
     * }
     */
    @PostMapping("/{promId}/cycle")
    public ResponseEntity<Map<String, Object>> createCycle(
            HttpServletRequest request,
            @PathVariable int promId,
            @RequestBody CalendDto calendDto) {

        Integer userId = getUserIdFromRequest(request);
        Map<String, Object> result = promService.createCycleProm(calendDto, promId, userId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // ============================================
    // [11] 반복 약속 전체조회 (PM-10)
    // ============================================

    /**
     * PM-10 반복 약속 전체조회
     * GET /api/promise/{promId}/cycle
     */
    @GetMapping("/{promId}/cycle")
    public ResponseEntity<Map<String, Object>> getCycles(
            HttpServletRequest request,
            @PathVariable int promId) {

        Integer userId = getUserIdFromRequest(request);
        Map<String, Object> result = promService.getCycleProm(promId, userId);

        return ResponseEntity.ok(result);
    }

    // ============================================
    // [12] 반복 약속 상세조회 (PM-11)
    // ============================================

    /**
     * PM-11 반복 약속 상세조회
     * GET /api/promise/cycle/{calendId}
     */
    @GetMapping("/cycle/{calendId}")
    public ResponseEntity<Map<String, Object>> getCycleDetail(
            HttpServletRequest request,
            @PathVariable int calendId) {

        Integer userId = getUserIdFromRequest(request);
        Map<String, Object> result = promService.getDetailCycleProm(calendId, userId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // ============================================
    // [13] 반복 약속 수정 (PM-12)
    // ============================================

    /**
     * PM-12 반복 약속 수정
     * PUT /api/promise/cycle/{calendId}
     *
     * Request Body:
     * {
     *   "calend_cycle": "MONTHLY",
     *   "calend_start": "2024-12-01T00:00:00",
     *   "calend_end": "2025-12-31T23:59:59"
     * }
     */
    @PutMapping("/cycle/{calendId}")
    public ResponseEntity<Map<String, Object>> updateCycle(
            HttpServletRequest request,
            @PathVariable int calendId,
            @RequestBody CalendDto calendDto) {

        Integer userId = getUserIdFromRequest(request);
        calendDto.setCalend_id(calendId);

        Map<String, Object> result = promService.updateCycleProm(calendDto, userId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // ============================================
    // [14] 반복 약속 삭제 (PM-13)
    // ============================================

    /**
     * PM-13 반복 약속 삭제
     * DELETE /api/promise/cycle/{calendId}
     */
    @DeleteMapping("/cycle/{calendId}")
    public ResponseEntity<Map<String, Object>> deleteCycle(
            HttpServletRequest request,
            @PathVariable int calendId) {

        Integer userId = getUserIdFromRequest(request);
        Map<String, Object> result = promService.deleteCycleProm(calendId, userId);

        return ResponseEntity.ok(result);
    }
}