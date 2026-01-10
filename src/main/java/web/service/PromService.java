package web.service;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import web.model.dto.promise.*;
import web.model.entity.promise.*;
import web.model.entity.user.UsersEntity;
import web.repository.promise.*;
import web.repository.user.UsersRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 약속 관리 서비스
 * - 약속 생성/수정/삭제/조회
 * - 약속 공유 및 평가
 * - 반복 약속 관리
 */
@Service
@Transactional  // 모든 메서드에 트랜잭션 적용
@RequiredArgsConstructor  // final 필드 생성자 자동 생성
public class PromService {

    // ============================================
    // [*] DI (Dependency Injection) - 의존성 주입
    // ============================================

    private final PromRepository promRepository;        // 약속 Repository
    private final ShareRepository shareRepository;      // 공유 Repository
    private final CalendRepository calendRepository;    // 반복 약속 Repository
    private final EvalRepository evalRepository;        // 평가 Repository
    private final TempRepository tempRepository;        // 임시 사용자 Repository
    private final UsersRepository usersRepository;      // 사용자 Repository
    private final KakaoMapService kakaoMapService;      // 카카오맵 서비스

    // ============================================
    // [1] 약속 생성 (PM-01)
    // ============================================

    /**
     * PM-01 약속 생성
     * 새로운 약속을 생성하고 이동 경로 정보도 함께 제공
     *
     * @param promDto 약속 정보 DTO
     * @param userId 약속 생성 사용자 ID
     * @return Map<String, Object> 생성 결과
     *         - success: 성공 여부
     *         - message: 결과 메시지
     *         - promise: 생성된 약속 정보
     *         - routeInfo: 이동 경로 정보 (선택적)
     */
    public Map<String, Object> createProm(PromDto promDto, int userId) {
        // 결과를 담을 Map 생성
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 사용자 조회
            Optional<UsersEntity> userOpt = usersRepository.findById(userId);

            if (userOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "사용자를 찾을 수 없습니다.");
                return result;
            }

            UsersEntity user = userOpt.get();

            // 2. 약속 주소가 있으면 좌표 변환
            // 주소를 위도/경도로 변환하여 지도 표시 및 거리 계산에 사용
            if (promDto.getPromAddr() != null && !promDto.getPromAddr().isEmpty()) {
                Map<String, Double> coordinates =
                        kakaoMapService.getCoordinatesFromAddress(promDto.getPromAddr());

                if (coordinates != null) {
                    // 변환된 좌표를 DTO에 설정
                    promDto.setPromLat(coordinates.get("lat"));
                    promDto.setPromLng(coordinates.get("lng"));
                }
            }

            // 3. 약속 Entity 생성 및 저장
            // DTO를 Entity로 변환
            PromEntity promEntity = promDto.toEntity(user);
            PromEntity savedProm = promRepository.save(promEntity);

            result.put("success", true);
            result.put("message", "약속이 생성되었습니다.");
            result.put("promise", savedProm.toDto());

            // 4. 사용자 집 주소와 약속 장소가 모두 있으면 거리/시간 계산
            // 사용자의 이동 시간과 거리를 미리 계산하여 제공
            if (user.getAddr() != null && promDto.getPromAddr() != null) {
                Map<String, Object> routeInfo = calculateRoute(user, savedProm);

                if (routeInfo != null) {
                    result.put("routeInfo", routeInfo);
                }
            }

            return result;

        } catch (Exception e) {
            // 예외 발생 시 에러 메시지 반환
            result.put("success", false);
            result.put("message", "약속 생성 실패: " + e.getMessage());
            return result;
        }
    }

    /**
     * 사용자 집에서 약속 장소까지의 경로 정보 계산 (Private Helper 메서드)
     *
     * @param user 사용자
     * @param prom 약속
     * @return Map<String, Object> 경로 정보 (거리, 시간, 추천 교통수단)
     */
    private Map<String, Object> calculateRoute(UsersEntity user, PromEntity prom) {
        try {
            // 1. 사용자 집 주소를 좌표로 변환
            Map<String, Double> homeCoords =
                    kakaoMapService.getCoordinatesFromAddress(user.getAddr());

            // 좌표 변환 실패 시 null 반환
            if (homeCoords == null || prom.getPromLat() == null || prom.getPromLng() == null) {
                return null;
            }

            // 좌표 추출
            double homeLat = homeCoords.get("lat");
            double homeLng = homeCoords.get("lng");
            double promLat = prom.getPromLat();
            double promLng = prom.getPromLng();

            // 2. 거리 계산
            double distance = kakaoMapService.calculateDistance(
                    homeLat, homeLng, promLat, promLng
            );

            // 3. 사용자 설정에서 선호 교통수단 가져오기
            // (SetEntity에서 조회 필요 - 여기서는 기본값 사용)
            // TODO: SetEntity에서 사용자의 선호 교통수단 조회
            String preferredTraffic = "SUBWAY_AND_BUS";

            // 4. 최적 교통수단 추천
            // 거리와 선호도를 고려하여 가장 적합한 교통수단 추천
            String recommendedTraffic =
                    kakaoMapService.recommendTrafficType(distance, preferredTraffic);

            // 5. 추천 교통수단 기준으로 경로 정보 조회
            Map<String, Object> routeInfo = kakaoMapService.getRouteByTrafficType(
                    homeLat, homeLng, promLat, promLng, recommendedTraffic
            );

            return routeInfo;

        } catch (Exception e) {
            // 경로 계산 실패 시 에러 로그만 출력하고 null 반환
            System.err.println("경로 계산 실패: " + e.getMessage());
            return null;
        }
    }

    // ============================================
    // [2] 약속 수정 (PM-02)
    // ============================================

    /**
     * PM-02 약속 수정
     * 약속 생성자만 수정 가능
     *
     * @param promDto 수정할 약속 정보
     * @param userId 수정 요청 사용자 ID (권한 확인용)
     * @return Map<String, Object> 수정 결과
     */
    public Map<String, Object> updateProm(PromDto promDto, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 약속 조회
            Optional<PromEntity> promOpt = promRepository.findById(promDto.getPromId());

            if (promOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "약속을 찾을 수 없습니다.");
                return result;
            }

            PromEntity prom = promOpt.get();

            // 2. 권한 확인 (약속 생성자만 수정 가능)
            if (prom.getUsersEntity().getUserId() != userId) {
                result.put("success", false);
                result.put("message", "약속을 수정할 권한이 없습니다.");
                return result;
            }

            // 3. 수정 가능한 필드 업데이트
            // null이 아닌 값만 업데이트 (부분 수정 지원)
            if (promDto.getPromTitle() != null) {
                prom.setPromTitle(promDto.getPromTitle());
            }
            if (promDto.getPromDate() != null) {
                prom.setPromDate(promDto.getPromDate());
            }
            if (promDto.getPromAlert() >= 0) {
                prom.setPromAlert(promDto.getPromAlert());
            }
            if (promDto.getPromAddr() != null) {
                prom.setPromAddr(promDto.getPromAddr());

                // 주소 변경 시 좌표 재계산
                Map<String, Double> coordinates =
                        kakaoMapService.getCoordinatesFromAddress(promDto.getPromAddr());

                if (coordinates != null) {
                    prom.setPromLat(coordinates.get("lat"));
                    prom.setPromLng(coordinates.get("lng"));
                }
            }
            if (promDto.getPromAddrDetail() != null) {
                prom.setPromAddrDetail(promDto.getPromAddrDetail());
            }
            if (promDto.getPromText() != null) {
                prom.setPromText(promDto.getPromText());
            }

            // 4. 저장 (Dirty Checking으로 자동 UPDATE)
            // @Transactional 환경에서는 엔티티 변경만으로도 자동 업데이트됨
            PromEntity updatedProm = promRepository.save(prom);

            result.put("success", true);
            result.put("message", "약속이 수정되었습니다.");
            result.put("promise", updatedProm.toDto());

            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "약속 수정 실패: " + e.getMessage());
            return result;
        }
    }

    // ============================================
    // [3] 약속 메모 추가 (PM-03)
    // ============================================

    /**
     * PM-03 약속 메모 추가
     * 기존 메모에 새 메모를 추가 (누적)
     *
     * @param promId 약속 ID
     * @param memo 메모 내용
     * @param userId 사용자 ID
     * @return Map<String, Object> 수정 결과
     */
    public Map<String, Object> memoProm(int promId, String memo, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 약속 조회
            Optional<PromEntity> promOpt = promRepository.findById(promId);

            if (promOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "약속을 찾을 수 없습니다.");
                return result;
            }

            PromEntity prom = promOpt.get();

            // 2. 권한 확인
            if (prom.getUsersEntity().getUserId() != userId) {
                result.put("success", false);
                result.put("message", "메모를 추가할 권한이 없습니다.");
                return result;
            }

            // 3. 메모 추가 (기존 메모가 있으면 추가)
            String currentMemo = prom.getPromMemo();
            if (currentMemo != null && !currentMemo.isEmpty()) {
                // 기존 메모 + 줄바꿈 + 새 메모
                prom.setPromMemo(currentMemo + "\n" + memo);
            } else {
                // 첫 메모
                prom.setPromMemo(memo);
            }

            // 4. 저장
            promRepository.save(prom);

            result.put("success", true);
            result.put("message", "메모가 추가되었습니다.");
            result.put("memo", prom.getPromMemo());

            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "메모 추가 실패: " + e.getMessage());
            return result;
        }
    }

    // ============================================
    // [4] 약속 취소 (PM-04)
    // ============================================

    /**
     * PM-04 약속 취소 (삭제)
     * 약속과 관련된 모든 데이터 삭제
     *
     * @param promId 약속 ID
     * @param userId 사용자 ID
     * @return Map<String, Object> 삭제 결과
     */
    public Map<String, Object> deleteProm(int promId, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 약속 조회
            Optional<PromEntity> promOpt = promRepository.findById(promId);

            if (promOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "약속을 찾을 수 없습니다.");
                return result;
            }

            PromEntity prom = promOpt.get();

            // 2. 권한 확인
            if (prom.getUsersEntity().getUserId() != userId) {
                result.put("success", false);
                result.put("message", "약속을 취소할 권한이 없습니다.");
                return result;
            }

            // 3. 약속 삭제
            // 연관된 Share, Calend도 Cascade로 삭제되도록 설정 필요
            // @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
            promRepository.deleteById(promId);

            result.put("success", true);
            result.put("message", "약속이 취소되었습니다.");

            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "약속 취소 실패: " + e.getMessage());
            return result;
        }
    }

    // ============================================
    // [5] 약속 전체조회 (PM-05)
    // ============================================

    /**
     * PM-05 약속 전체조회
     * 사용자의 약속 목록 조회 (날짜 범위 필터링 가능)
     *
     * @param userId 사용자 ID
     * @param startDate 조회 시작 날짜 (옵션)
     * @param endDate 조회 종료 날짜 (옵션)
     * @return Map<String, Object> 약속 목록
     */
    public Map<String, Object> getProm(int userId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 사용자 조회
            Optional<UsersEntity> userOpt = usersRepository.findById(userId);

            if (userOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "사용자를 찾을 수 없습니다.");
                return result;
            }

            UsersEntity user = userOpt.get();
            List<PromEntity> promList;

            // 2. 날짜 범위가 있으면 범위 조회, 없으면 전체 조회
            if (startDate != null && endDate != null) {
                // 특정 기간의 약속 조회 (캘린더 월별 조회 등에 사용)
                promList = promRepository.findByUsersEntityAndPromDateBetween(
                        user, startDate, endDate
                );
            } else {
                // 모든 약속 조회 (날짜 순으로 정렬)
                promList = promRepository.findByUsersEntityOrderByPromDateAsc(user);
            }

            // 3. Entity 리스트를 DTO 리스트로 변환
            List<PromDto> promDtoList = promList.stream()
                    .map(PromEntity::toDto)
                    .collect(Collectors.toList());

            result.put("success", true);
            result.put("promiseList", promDtoList);
            result.put("totalCount", promDtoList.size());

            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "약속 조회 실패: " + e.getMessage());
            return result;
        }
    }

    // PromService.java 계속...

    // ============================================
    // [6] 약속 상세조회 (PM-06)
    // ============================================

    /**
     * PM-06 약속 상세조회
     * 약속 정보와 함께 공유 정보, 반복 정보도 조회
     *
     * @param promId 약속 ID
     * @param userId 사용자 ID (권한 확인용)
     * @return Map<String, Object> 약속 상세 정보
     */
    public Map<String, Object> getDetailProm(int promId, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 약속 조회
            Optional<PromEntity> promOpt = promRepository.findById(promId);

            if (promOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "약속을 찾을 수 없습니다.");
                return result;
            }

            PromEntity prom = promOpt.get();

            // 2. 권한 확인 (본인 약속만 조회 가능)
            if (prom.getUsersEntity().getUserId() != userId) {
                result.put("success", false);
                result.put("message", "약속을 조회할 권한이 없습니다.");
                return result;
            }

            // 3. 약속 정보 반환
            result.put("success", true);
            result.put("promise", prom.toDto());

            // 4. 공유 정보도 함께 조회
            // 이 약속을 누구와 공유했는지 확인
            List<ShareEntity> shareList = shareRepository.findByPromEntity(prom);
            List<ShareDto> shareDtoList = shareList.stream()
                    .map(ShareEntity::toDto)
                    .collect(Collectors.toList());

            result.put("shareList", shareDtoList);
            result.put("shareCount", shareDtoList.size());

            // 5. 반복 약속 정보도 조회
            // 주간, 월간 반복 약속 설정 확인
            List<CalendEntity> calendList = calendRepository.findByPromEntity(prom);
            List<CalendDto> calendDtoList = calendList.stream()
                    .map(CalendEntity::toDto)
                    .collect(Collectors.toList());

            result.put("cycleList", calendDtoList);

            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "약속 조회 실패: " + e.getMessage());
            return result;
        }
    }

    // ============================================
    // [7] 약속 공유 (PM-07)
    // ============================================

    /**
     * PM-07 약속 공유
     * 약속을 공유할 수 있는 링크 생성 (카카오톡 공유 등에 사용)
     *
     * @param promId 약속 ID
     * @param userId 사용자 ID
     * @return Map<String, Object> 공유 링크 정보
     */
    public Map<String, Object> shareProm(int promId, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 약속 조회
            Optional<PromEntity> promOpt = promRepository.findById(promId);

            if (promOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "약속을 찾을 수 없습니다.");
                return result;
            }

            PromEntity prom = promOpt.get();

            // 2. 권한 확인
            if (prom.getUsersEntity().getUserId() != userId) {
                result.put("success", false);
                result.put("message", "약속을 공유할 권한이 없습니다.");
                return result;
            }

            // 3. Share Entity 생성 (토큰은 자동 생성됨)
            // UUID를 사용하여 고유한 공유 토큰 생성
            ShareEntity share = ShareEntity.builder()
                    .promEntity(prom)
                    .build();

            ShareEntity savedShare = shareRepository.save(share);

            // 4. 공유 링크 생성
            String shareUrl = "https://yourdomain.com/promise/share/" + savedShare.getShareToken();

            // 5. 카카오톡 공유 정보 생성
            // 카카오톡 링크 공유 API에 필요한 정보 구성
            Map<String, String> kakaoShareInfo = new HashMap<>();
            kakaoShareInfo.put("title", prom.getPromTitle());
            kakaoShareInfo.put("description", prom.getPromText());
            kakaoShareInfo.put("link", shareUrl);

            if (prom.getPromDate() != null) {
                kakaoShareInfo.put("date", prom.getPromDate().toString());
            }
            if (prom.getPromAddr() != null) {
                kakaoShareInfo.put("location", prom.getPromAddr());
            }

            result.put("success", true);
            result.put("message", "약속이 공유되었습니다.");
            result.put("share", savedShare.toDto());
            result.put("shareUrl", shareUrl);
            result.put("kakaoShareInfo", kakaoShareInfo);

            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "약속 공유 실패: " + e.getMessage());
            return result;
        }
    }

    // ============================================
    // [8] 약속 평가 (PM-08)
    // ============================================

    /**
     * PM-08 약속 평가
     * 약속 참여자가 약속에 대한 평가 등록 (약속 종료 후 하루까지)
     *
     * @param evalDto 평가 정보
     * @param shareId 공유 ID
     * @param isTemp 임시 사용자 여부
     * @return Map<String, Object> 평가 결과
     */
    public Map<String, Object> evalProm(PromEvaluationDto evalDto, int shareId, boolean isTemp) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. Share 조회
            Optional<ShareEntity> shareOpt = shareRepository.findById(shareId);

            if (shareOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "공유된 약속을 찾을 수 없습니다.");
                return result;
            }

            ShareEntity share = shareOpt.get();

            // 2. 약속 종료 후 하루까지만 평가 가능
            LocalDateTime promDate = share.getPromEntity().getPromDate();
            LocalDateTime now = LocalDateTime.now();

            if (promDate != null && now.isAfter(promDate.plusDays(1))) {
                result.put("success", false);
                result.put("message", "평가 기간이 종료되었습니다. (약속 종료 후 하루까지 가능)");
                return result;
            }

            // 3. 중복 평가 방지 확인
            if (evalRepository.existsByShareEntity(share)) {
                result.put("success", false);
                result.put("message", "이미 평가가 완료된 약속입니다.");
                return result;
            }

            EvalEntity eval;

            // 4-1. 회원 사용자 평가
            if (!isTemp && evalDto.getUserId() != null) {
                Optional<UsersEntity> userOpt = usersRepository.findById(evalDto.getUserId());

                if (userOpt.isEmpty()) {
                    result.put("success", false);
                    result.put("message", "사용자를 찾을 수 없습니다.");
                    return result;
                }

                // 회원의 중복 평가 확인
                Optional<EvalEntity> existingEval =
                        evalRepository.findByShareEntityAndUsersEntity(share, userOpt.get());

                if (existingEval.isPresent()) {
                    result.put("success", false);
                    result.put("message", "이미 이 약속을 평가하셨습니다.");
                    return result;
                }

                // 평가 Entity 생성
                eval = EvalEntity.builder()
                        .usersEntity(userOpt.get())
                        .shareEntity(share)
                        .build();
            }
            // 4-2. 임시 사용자 평가
            else {
                // 임시 사용자 생성 또는 조회
                TempEntity temp;

                if (evalDto.getTempId() != null && evalDto.getTempId() > 0) {
                    // 기존 임시 사용자
                    temp = tempRepository.findById(evalDto.getTempId()).orElse(null);
                } else {
                    // 새로운 임시 사용자 생성
                    TempEntity.TempEntityBuilder tempBuilder = TempEntity.builder();

                    // 임시 사용자 이름이 제공되면 설정
                    if (evalDto.getTempName() != null && !evalDto.getTempName().isEmpty()) {
                        tempBuilder.tempName(evalDto.getTempName());
                    }

                    temp = tempBuilder.build();
                    temp = tempRepository.save(temp);
                }

                if (temp == null) {
                    result.put("success", false);
                    result.put("message", "임시 사용자 생성 실패");
                    return result;
                }

                eval = EvalEntity.builder()
                        .tempEntity(temp)
                        .shareEntity(share)
                        .build();
            }

            // 5. 평가 저장
            EvalEntity savedEval = evalRepository.save(eval);

            // 6. Share 테이블의 평가 정보 업데이트
            share.setShareCheck(evalDto.getShareCheck());
            share.setShareScore(evalDto.getShareScore() > 0 ? evalDto.getShareScore() : 3);

            if (evalDto.getShareFeedback() != null && !evalDto.getShareFeedback().isEmpty()) {
                share.setShareFeedback(evalDto.getShareFeedback());
            }

            shareRepository.save(share);

            result.put("success", true);
            result.put("message", "평가가 완료되었습니다.");
            result.put("eval", savedEval.toDto());
            result.put("share", share.toDto());

            // 7. 임시 사용자인 경우 회원가입 제안
            if (isTemp) {
                result.put("isTemp", true);
                result.put("signupSuggestion", "회원가입하고 더 많은 기능을 이용하세요!");
                result.put("signupUrl", "https://yourdomain.com/signup");
            }

            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "평가 실패: " + e.getMessage());
            return result;
        }
    }

    // ============================================
    // [9] 반복 약속 등록 (PM-09)
    // ============================================

    /**
     * PM-09 반복 약속 등록
     * 매주, 매월 등 반복되는 약속 설정
     *
     * @param calendDto 반복 약속 정보
     * @param promId 약속 ID
     * @param userId 사용자 ID
     * @return Map<String, Object> 등록 결과
     */
    public Map<String, Object> createCycleProm(CalendDto calendDto, int promId, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 약속 조회
            Optional<PromEntity> promOpt = promRepository.findById(promId);

            if (promOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "약속을 찾을 수 없습니다.");
                return result;
            }

            PromEntity prom = promOpt.get();

            // 2. 권한 확인
            if (prom.getUsersEntity().getUserId() != userId) {
                result.put("success", false);
                result.put("message", "반복 약속을 등록할 권한이 없습니다.");
                return result;
            }

            // 3. CalendEntity 생성 및 저장
            CalendEntity calend = calendDto.toEntity(prom);
            CalendEntity savedCalend = calendRepository.save(calend);

            result.put("success", true);
            result.put("message", "반복 약속이 등록되었습니다.");
            result.put("cycle", savedCalend.toDto());

            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "반복 약속 등록 실패: " + e.getMessage());
            return result;
        }
    }

    // ============================================
    // [10~13] 반복 약속 조회/수정/삭제
    // ============================================

    /**
     * PM-10 반복 약속 전체조회
     */
    public Map<String, Object> getCycleProm(int promId, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            Optional<PromEntity> promOpt = promRepository.findById(promId);
            if (promOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "약속을 찾을 수 없습니다.");
                return result;
            }

            PromEntity prom = promOpt.get();
            if (prom.getUsersEntity().getUserId() != userId) {
                result.put("success", false);
                result.put("message", "반복 약속을 조회할 권한이 없습니다.");
                return result;
            }

            List<CalendEntity> calendList = calendRepository.findByPromEntity(prom);
            List<CalendDto> calendDtoList = calendList.stream()
                    .map(CalendEntity::toDto)
                    .collect(Collectors.toList());

            result.put("success", true);
            result.put("cycleList", calendDtoList);
            result.put("totalCount", calendDtoList.size());

            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "반복 약속 조회 실패: " + e.getMessage());
            return result;
        }
    }

    /**
     * PM-11 반복 약속 상세조회
     */
    public Map<String, Object> getDetailCycleProm(int calendId, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            Optional<CalendEntity> calendOpt = calendRepository.findById(calendId);
            if (calendOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "반복 약속을 찾을 수 없습니다.");
                return result;
            }

            CalendEntity calend = calendOpt.get();
            PromEntity prom = calend.getPromEntity();

            if (prom.getUsersEntity().getUserId() != userId) {
                result.put("success", false);
                result.put("message", "반복 약속을 조회할 권한이 없습니다.");
                return result;
            }

            result.put("success", true);
            result.put("cycle", calend.toDto());
            result.put("promise", prom.toDto());

            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "반복 약속 조회 실패: " + e.getMessage());
            return result;
        }
    }

    /**
     * PM-12 반복 약속 수정
     */
    public Map<String, Object> updateCycleProm(CalendDto calendDto, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            Optional<CalendEntity> calendOpt = calendRepository.findById(calendDto.getCalendId());
            if (calendOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "반복 약속을 찾을 수 없습니다.");
                return result;
            }

            CalendEntity calend = calendOpt.get();
            PromEntity prom = calend.getPromEntity();

            if (prom.getUsersEntity().getUserId() != userId) {
                result.put("success", false);
                result.put("message", "반복 약속을 수정할 권한이 없습니다.");
                return result;
            }

            // 수정 가능한 필드 업데이트
            if (calendDto.getCalendCycle() != null) {
                calend.setCalendCycle(calendDto.getCalendCycle());
            }
            if (calendDto.getCalendStart() != null) {
                calend.setCalendStart(calendDto.getCalendStart());
            }
            if (calendDto.getCalendEnd() != null) {
                calend.setCalendEnd(calendDto.getCalendEnd());
            }

            CalendEntity updatedCalend = calendRepository.save(calend);

            result.put("success", true);
            result.put("message", "반복 약속이 수정되었습니다.");
            result.put("cycle", updatedCalend.toDto());

            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "반복 약속 수정 실패: " + e.getMessage());
            return result;
        }
    }

    /**
     * PM-13 반복 약속 삭제
     */
    public Map<String, Object> deleteCycleProm(int calendId, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            Optional<CalendEntity> calendOpt = calendRepository.findById(calendId);
            if (calendOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "반복 약속을 찾을 수 없습니다.");
                return result;
            }

            CalendEntity calend = calendOpt.get();
            PromEntity prom = calend.getPromEntity();

            if (prom.getUsersEntity().getUserId() != userId) {
                result.put("success", false);
                result.put("message", "반복 약속을 삭제할 권한이 없습니다.");
                return result;
            }

            calendRepository.deleteById(calendId);

            result.put("success", true);
            result.put("message", "반복 약속이 삭제되었습니다.");

            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "반복 약속 삭제 실패: " + e.getMessage());
            return result;
        }
    }

    // ============================================
    // [14] 공유 토큰으로 약속 조회 (공개 API용)
    // ============================================

    /**
     * 공유 토큰으로 약속 조회 (비회원도 접근 가능)
     * 카카오톡 등으로 공유된 링크를 통해 약속 정보 확인
     *
     * @param shareToken 공유 토큰
     * @return Map<String, Object> 약속 정보
     */
    public Map<String, Object> getPromByShareToken(String shareToken) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. Share 토큰으로 조회
            // TODO: Repository에 findByShareToken 메서드 추가 필요
            List<ShareEntity> allShares = shareRepository.findAll();
            Optional<ShareEntity> shareOpt = allShares.stream()
                    .filter(s -> s.getShareToken().equals(shareToken))
                    .findFirst();

            if (shareOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "유효하지 않은 공유 링크입니다.");
                return result;
            }

            ShareEntity share = shareOpt.get();
            PromEntity prom = share.getPromEntity();

            // 2. 약속 정보 반환 (민감한 정보 제외)
            // 비회원도 볼 수 있으므로 필요한 정보만 제공
            Map<String, Object> promInfo = new HashMap<>();
            promInfo.put("title", prom.getPromTitle());
            promInfo.put("date", prom.getPromDate());
            promInfo.put("location", prom.getPromAddr());
            promInfo.put("locationDetail", prom.getPromAddrDetail());
            promInfo.put("text", prom.getPromText());

            result.put("success", true);
            result.put("promise", promInfo);
            result.put("shareId", share.getShareId());

            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "약속 조회 실패: " + e.getMessage());
            return result;
        }
    }
}