package web.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.model.dto.user.AtenDto;
import web.model.dto.user.FrenDto;
import web.model.dto.user.SetDto;
import web.model.dto.user.UsersDto;
import web.model.entity.common.LangType;
import web.model.entity.common.TrafficType;
import web.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 사용자 관련 API 컨트롤러 (인증 필요)
 * - 내 정보 조회/수정, 출석, 친구, 설정 등
 * - 모든 API는 JWT 인증 필요 (Authorization: Bearer <token>)
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * HttpServletRequest에서 사용자 ID 추출 헬퍼 메서드
     * JwtInterceptor에서 request에 저장한 userId를 가져옴
     */
    private Integer getUserIdFromRequest(HttpServletRequest request) {
        return (Integer) request.getAttribute("userId");
    }

    // ============================================
    // [1] 내 정보 관련 API
    // ============================================

    /**
     * 내 정보 조회
     * GET /api/user/me
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyInfo(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer userId = getUserIdFromRequest(request);
            UsersDto user = userService.getMyInfo(userId);

            if (user != null) {
                response.put("success", true);
                response.put("user", user);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "사용자 정보를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 내 정보 수정
     * PUT /api/user/me
     *
     * Request Body:
     * {
     *   "password": "newPassword123",
     *   "user_name": "새이름",
     *   "phone": "010-9999-9999",
     *   "addr": "새주소",
     *   "addr_detail": "상세주소"
     * }
     */
    @PutMapping("/me")
    public ResponseEntity<Map<String, Object>> updateMyInfo(
            HttpServletRequest request,
            @RequestBody UsersDto usersDto) {

        Map<String, Object> response = new HashMap<>();

        try {
            Integer userId = getUserIdFromRequest(request);
            usersDto.setUserId(userId);  // 현재 로그인한 사용자 ID 설정

            UsersDto updatedUser = userService.updateMyInfo(usersDto);

            if (updatedUser != null) {
                response.put("success", true);
                response.put("message", "정보 수정 성공");
                response.put("user", updatedUser);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "정보 수정 실패");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 회원 탈퇴
     * DELETE /api/user/me
     */
    @DeleteMapping("/me")
    public ResponseEntity<Map<String, Object>> deleteAccount(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer userId = getUserIdFromRequest(request);
            boolean success = userService.deleteUserState(userId);

            response.put("success", success);
            response.put("message", success ? "회원 탈퇴 성공" : "회원 탈퇴 실패");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // ============================================
    // [2] 출석 관련 API
    // ============================================

    /**
     * 출석하기
     * POST /api/user/attendance
     */
    @PostMapping("/attendance")
    public ResponseEntity<Map<String, Object>> checkAttendance(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer userId = getUserIdFromRequest(request);
            AtenDto attendance = userService.aten(userId);

            if (attendance != null) {
                response.put("success", true);
                response.put("message", "출석 완료");
                response.put("attendance", attendance);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "이미 오늘 출석했습니다.");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 출석 기록 조회
     * GET /api/user/attendance
     */
    @GetMapping("/attendance")
    public ResponseEntity<Map<String, Object>> getAttendance(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer userId = getUserIdFromRequest(request);
            List<AtenDto> attendanceList = userService.getAten(userId);

            response.put("success", true);
            response.put("attendanceList", attendanceList);
            response.put("totalDays", attendanceList.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // ============================================
    // [3] 친구 관련 API
    // ============================================

    /**
     * 친구 요청
     * POST /api/user/friends/request
     *
     * Request Body:
     * {
     *   "receiverUserId": 2
     * }
     */
    @PostMapping("/friends/request")
    public ResponseEntity<Map<String, Object>> requestFriend(
            HttpServletRequest request,
            @RequestBody Map<String, Integer> data) {

        Map<String, Object> response = new HashMap<>();

        try {
            Integer offerUserId = getUserIdFromRequest(request);
            Integer receiverUserId = data.get("receiverUserId");

            FrenDto friendship = userService.offerFren(offerUserId, receiverUserId);

            if (friendship != null) {
                response.put("success", true);
                response.put("message", "친구 요청 성공");
                response.put("friendship", friendship);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "친구 요청 실패 (이미 친구이거나 요청 중)");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 친구 수락
     * POST /api/user/friends/accept/{frenId}
     */
    @PostMapping("/friends/accept/{frenId}")
    public ResponseEntity<Map<String, Object>> acceptFriend(
            HttpServletRequest request,
            @PathVariable int frenId) {

        Map<String, Object> response = new HashMap<>();

        try {
            Integer userId = getUserIdFromRequest(request);
            FrenDto friendship = userService.receiveFren(frenId, userId);

            if (friendship != null) {
                response.put("success", true);
                response.put("message", "친구 수락 성공");
                response.put("friendship", friendship);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "친구 수락 실패");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 친구 거절
     * POST /api/user/friends/reject/{frenId}
     */
    @PostMapping("/friends/reject/{frenId}")
    public ResponseEntity<Map<String, Object>> rejectFriend(
            HttpServletRequest request,
            @PathVariable int frenId) {

        Map<String, Object> response = new HashMap<>();

        try {
            Integer userId = getUserIdFromRequest(request);
            boolean success = userService.negativeFren(frenId, userId);

            response.put("success", success);
            response.put("message", success ? "친구 거절 성공" : "친구 거절 실패");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 친구 목록 조회
     * GET /api/user/friends
     */
    @GetMapping("/friends")
    public ResponseEntity<Map<String, Object>> getFriends(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer userId = getUserIdFromRequest(request);
            List<FrenDto> friendList = userService.getFren(userId);

            response.put("success", true);
            response.put("friendList", friendList);
            response.put("totalFriends", friendList.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 친구 상세 조회
     * GET /api/user/friends/{frenId}
     */
    @GetMapping("/friends/{frenId}")
    public ResponseEntity<Map<String, Object>> getFriendDetail(@PathVariable int frenId) {
        Map<String, Object> response = new HashMap<>();

        try {
            FrenDto friendship = userService.getDetailFren(frenId);

            if (friendship != null) {
                response.put("success", true);
                response.put("friendship", friendship);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "친구 정보를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 친구 삭제
     * DELETE /api/user/friends/{frenId}
     */
    @DeleteMapping("/friends/{frenId}")
    public ResponseEntity<Map<String, Object>> deleteFriend(
            HttpServletRequest request,
            @PathVariable int frenId) {

        Map<String, Object> response = new HashMap<>();

        try {
            Integer userId = getUserIdFromRequest(request);
            boolean success = userService.deleteFren(frenId, userId);

            response.put("success", success);
            response.put("message", success ? "친구 삭제 성공" : "친구 삭제 실패");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // ============================================
    // [4] 설정 관련 API
    // ============================================

    /**
     * 설정 조회
     * GET /api/user/settings
     */
    @GetMapping("/settings")
    public ResponseEntity<Map<String, Object>> getSettings(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer userId = getUserIdFromRequest(request);
            SetDto settings = userService.getSetting(userId);

            if (settings != null) {
                response.put("success", true);
                response.put("settings", settings);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "설정 정보를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 리마인드 설정
     * PUT /api/user/settings/remind
     *
     * Request Body:
     * {
     *   "remindMinutes": 30
     * }
     */
    @PutMapping("/settings/remind")
    public ResponseEntity<Map<String, Object>> setRemind(
            HttpServletRequest request,
            @RequestBody Map<String, Integer> data) {

        Map<String, Object> response = new HashMap<>();

        try {
            Integer userId = getUserIdFromRequest(request);
            Integer remindMinutes = data.get("remindMinutes");

            boolean success = userService.setRemind(userId, remindMinutes);

            response.put("success", success);
            response.put("message", success ? "리마인드 설정 완료" : "설정 실패");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 업무표시 설정
     * PUT /api/user/settings/work
     *
     * Request Body:
     * {
     *   "workDisplay": 1
     * }
     */
    @PutMapping("/settings/work")
    public ResponseEntity<Map<String, Object>> setWork(
            HttpServletRequest request,
            @RequestBody Map<String, Integer> data) {

        Map<String, Object> response = new HashMap<>();

        try {
            Integer userId = getUserIdFromRequest(request);
            Integer workDisplay = data.get("workDisplay");

            boolean success = userService.setWork(userId, workDisplay);

            response.put("success", success);
            response.put("message", success ? "업무표시 설정 완료" : "설정 실패");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 교통수단 설정
     * PUT /api/user/settings/traffic
     *
     * Request Body:
     * {
     *   "trafficType": "SUBWAY"
     * }
     */
    @PutMapping("/settings/traffic")
    public ResponseEntity<Map<String, Object>> setTraffic(
            HttpServletRequest request,
            @RequestBody Map<String, String> data) {

        Map<String, Object> response = new HashMap<>();

        try {
            Integer userId = getUserIdFromRequest(request);
            TrafficType trafficType = TrafficType.valueOf(data.get("trafficType"));

            boolean success = userService.setTraffic(userId, trafficType);

            response.put("success", success);
            response.put("message", success ? "교통수단 설정 완료" : "설정 실패");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 언어 설정
     * PUT /api/user/settings/language
     *
     * Request Body:
     * {
     *   "language": "KOREAN"
     * }
     */
    @PutMapping("/settings/language")
    public ResponseEntity<Map<String, Object>> setLanguage(
            HttpServletRequest request,
            @RequestBody Map<String, String> data) {

        Map<String, Object> response = new HashMap<>();

        try {
            Integer userId = getUserIdFromRequest(request);
            LangType langType = LangType.valueOf(data.get("language"));

            boolean success = userService.setLang(userId, langType);

            response.put("success", success);
            response.put("message", success ? "언어 설정 완료" : "설정 실패");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 설정 초기화
     * POST /api/user/settings/reset
     */
    @PostMapping("/settings/reset")
    public ResponseEntity<Map<String, Object>> resetSettings(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer userId = getUserIdFromRequest(request);
            boolean success = userService.setReset(userId);

            response.put("success", success);
            response.put("message", success ? "설정 초기화 완료" : "초기화 실패");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}