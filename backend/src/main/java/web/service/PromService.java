package web.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import web.model.dto.promise.*;
import web.model.entity.common.CycleType;
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
@Transactional
@RequiredArgsConstructor
public class PromService {

    // ============================================
    // [*] DI (Dependency Injection)
    // ============================================
    private final PromRepository promRepository;
    private final ShareRepository shareRepository;
    private final CalendRepository calendRepository;
    private final EvalRepository evalRepository;
    private final TempRepository tempRepository;
    private final UsersRepository usersRepository;
    private final KakaoMapService kakaoMapService;

    // ============================================
    // [1] 약속 생성 (PM-01)
    // ============================================

    /**
     * PM-01 약속 생성
     * @param promDto 약속 정보
     * @param userId 약속 생성 사용자 ID
     * @return 생성된 약속 정보 + 이동 거리/시간 정보
     */
    public Map<String, Object> createProm(PromDto promDto, int userId) {
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
            if (promDto.getPromAddr() != null && !promDto.getPromAddr().isEmpty()) {
                Map<String, Double> coordinates =
                        kakaoMapService.getCoordinatesFromAddress(promDto.getPromAddr());

                if (coordinates != null) {
                    promDto.setPromLat(coordinates.get("lat"));
                    promDto.setPromLng(coordinates.get("lng"));
                }
            }

            // 3. 약속 Entity 생성 및 저장
            PromEntity promEntity = promDto.toEntity(user);
            PromEntity savedProm = promRepository.save(promEntity);

            result.put("success", true);
            result.put("message", "약속이 생성되었습니다.");
            result.put("promise", savedProm.toDto());

            // 4. 사용자 집 주소와 약속 장소가 모두 있으면 거리/시간 계산
            if (user.getAddr() != null && promDto.getPromAddr() != null) {
                Map<String, Object> routeInfo = calculateRoute(user, savedProm);

                if (routeInfo != null) {
                    result.put("routeInfo", routeInfo);
                }
            }

            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "약속 생성 실패: " + e.getMessage());
            return result;
        }
    }

    /**
     * 사용자 집에서 약속 장소까지의 경로 정보 계산
     * @param user 사용자
     * @param prom 약속
     * @return 경로 정보 (거리, 시간, 추천 교통수단)
     */
    private Map<String, Object> calculateRoute(UsersEntity user, PromEntity prom) {
        try {
            // 1. 사용자 집 주소를 좌표로 변환
            Map<String, Double> homeCoords =
                    kakaoMapService.getCoordinatesFromAddress(user.getAddr());

            if (homeCoords == null || prom.getPromLat() == null || prom.getPromLng() == null) {
                return null;
            }

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
            String preferredTraffic = "SUBWAY_AND_BUS";

            // 4. 최적 교통수단 추천
            String recommendedTraffic =
                    kakaoMapService.recommendTrafficType(distance, preferredTraffic);

            // 5. 추천 교통수단 기준으로 경로 정보 조회
            Map<String, Object> routeInfo = kakaoMapService.getRouteByTrafficType(
                    homeLat, homeLng, promLat, promLng, recommendedTraffic
            );

            return routeInfo;

        } catch (Exception e) {
            System.err.println("경로 계산 실패: " + e.getMessage());
            return null;
        }
    }

    // ============================================
    // [2] 약속 수정 (PM-02)
    // ============================================

    /**
     * PM-02 약속 수정
     * @param promDto 수정할 약속 정보
     * @param userId 수정 요청 사용자 ID (권한 확인용)
     * @return 수정 결과
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
            if (promDto.getPromTitle() != null) {
                prom.setPromTitle(promDto.getPromTitle());
            }
            if (promDto.getPromDate() != null) {
                prom.setProm_date(promDto.getProm_date());
            }
            if (promDto.getProm_alert() >= 0) {
                prom.setProm_alert(promDto.getProm_alert());
            }
            if (promDto.getProm_addr() != null) {
                prom.setProm_addr(promDto.getProm_addr());

                // 주소 변경 시 좌표 재계산
                Map<String, Double> coordinates =
                        kakaoMapService.getCoordinatesFromAddress(promDto.getProm_addr());

                if (coordinates != null) {
                    prom.setProm_lat(coordinates.get("lat"));
                    prom.setProm_lng(coordinates.get("lng"));
                }
            }
            if (promDto.getProm_addr_detail() != null) {
                prom.setProm_addr_detail(promDto.getProm_addr_detail());
            }
            if (promDto.getProm_text() != null) {
                prom.setProm_text(promDto.getProm_text());
            }

            // 4. 저장 (Dirty Checking으로 자동 UPDATE)
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
     * @param promId 약속 ID
     * @param memo 메모 내용
     * @param userId 사용자 ID
     * @return 수정 결과
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
            if (prom.getUsersEntity().getUser_id() != userId) {
                result.put("success", false);
                result.put("message", "메모를 추가할 권한이 없습니다.");
                return result;
            }

            // 3. 메모 추가 (기존 메모가 있으면 추가)
            String currentMemo = prom.getProm_memo();
            if (currentMemo != null && !currentMemo.isEmpty()) {
                prom.setProm_memo(currentMemo + "\n" + memo);
            } else {
                prom.setProm_memo(memo);
            }

            // 4. 저장
            promRepository.save(prom);

            result.put("success", true);
            result.put("message", "메모가 추가되었습니다.");
            result.put("memo", prom.getProm_memo());

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
     * @param promId 약속 ID
     * @param userId 사용자 ID
     * @return 삭제 결과
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
            if (prom.getUsersEntity().getUser_id() != userId) {
                result.put("success", false);
                result.put("message", "약속을 취소할 권한이 없습니다.");
                return result;
            }

            // 3. 약속 삭제
            // 연관된 Share, Calend도 Cascade로 삭제되도록 설정 필요
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
     * @param userId 사용자 ID
     * @param startDate 조회 시작 날짜 (옵션)
     * @param endDate 조회 종료 날짜 (옵션)
     * @return 약속 목록
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
                promList = promRepository.findByUsersEntityAndPromDateBetween(
                        user, startDate, endDate
                );
            } else {
                // 날짜 순으로 정렬하여 조회
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

    // ============================================
    // [6] 약속 상세조회 (PM-06)
    // ============================================

    /**
     * PM-06 약속 상세조회
     * @param promId 약속 ID
     * @param userId 사용자 ID (권한 확인용)
     * @return 약속 상세 정보
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
            if (prom.getUsersEntity().getUser_id() != userId) {
                result.put("success", false);
                result.put("message", "약속을 조회할 권한이 없습니다.");
                return result;
            }

            // 3. 약속 정보 반환
            result.put("success", true);
            result.put("promise", prom.toDto());

            // 4. 공유 정보도 함께 조회
            List<ShareEntity> shareList = shareRepository.findByPromEntity(prom);
            List<ShareDto> shareDtoList = shareList.stream()
                    .map(ShareEntity::toDto)
                    .collect(Collectors.toList());

            result.put("shareList", shareDtoList);
            result.put("shareCount", shareDtoList.size());

            // 5. 반복 약속 정보도 조회
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
     * @param promId 약속 ID
     * @param userId 사용자 ID
     * @return 공유 링크 정보
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
            if (prom.getUsersEntity().getUser_id() != userId) {
                result.put("success", false);
                result.put("message", "약속을 공유할 권한이 없습니다.");
                return result;
            }

            // 3. Share Entity 생성 (토큰은 자동 생성됨)
            ShareEntity share = ShareEntity.builder()
                    .promEntity(prom)
                    .build();

            ShareEntity savedShare = shareRepository.save(share);

            // 4. 공유 링크 생성
            String shareUrl = "https://yourdomain.com/promise/share/" + savedShare.getShare_token();

            // 5. 카카오톡 공유 정보 생성
            Map<String, String> kakaoShareInfo = new HashMap<>();
            kakaoShareInfo.put("title", prom.getProm_title());
            kakaoShareInfo.put("description", prom.getProm_text());
            kakaoShareInfo.put("link", shareUrl);

            if (prom.getProm_date() != null) {
                kakaoShareInfo.put("date", prom.getProm_date().toString());
            }
            if (prom.getProm_addr() != null) {
                kakaoShareInfo.put("location", prom.getProm_addr());
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
     * @param evalDto 평가 정보 (PromEvaluationDto 사용)
     * @param shareId 공유 ID
     * @param isTemp 임시 사용자 여부
     * @return 평가 결과
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
            LocalDateTime promDate = share.getPromEntity().getProm_date();
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
            if (!isTemp && evalDto.getUser_id() != null) {
                Optional<UsersEntity> userOpt = usersRepository.findById(evalDto.getUser_id());

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

                eval = EvalEntity.builder()
                        .usersEntity(userOpt.get())
                        .shareEntity(share)
                        .build();
            }
            // 4-2. 임시 사용자 평가
            else {
                // 임시 사용자 생성 또는 조회
                TempEntity temp;

                if (evalDto.getTemp_id() != null && evalDto.getTemp_id() > 0) {
                    // 기존 임시 사용자
                    temp = tempRepository.findById(evalDto.getTemp_id()).orElse(null);
                } else {
                    // 새로운 임시 사용자 생성
                    TempEntity.TempEntityBuilder tempBuilder = TempEntity.builder();

                    // 임시 사용자 이름이 제공되면 설정
                    if (evalDto.getTemp_name() != null && !evalDto.getTemp_name().isEmpty()) {
                        tempBuilder.temp_name(evalDto.getTemp_name());
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
            // evalDto에서 평가 정보 가져오기
            share.setShare_check(evalDto.getShare_check());
            share.setShare_score(evalDto.getShare_score() > 0 ? evalDto.getShare_score() : 3);

            if (evalDto.getShare_feedback() != null && !evalDto.getShare_feedback().isEmpty()) {
                share.setShare_feedback(evalDto.getShare_feedback());
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
     * @param calendDto 반복 약속 정보
     * @param promId 약속 ID
     * @param userId 사용자 ID
     * @return 등록 결과
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
            if (prom.getUsersEntity().getUser_id() != userId) {
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
    // [10] 반복 약속 전체조회 (PM-10)
    // ============================================

    /**
     * PM-10 반복 약속 전체조회
     * @param promId 약속 ID
     * @param userId 사용자 ID
     * @return 반복 약속 목록
     */
    public Map<String, Object> getCycleProm(int promId, int userId) {
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
            if (prom.getUsersEntity().getUser_id() != userId) {
                result.put("success", false);
                result.put("message", "반복 약속을 조회할 권한이 없습니다.");
                return result;
            }

            // 3. 반복 약속 목록 조회
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

    // ============================================
    // [11] 반복 약속 상세조회 (PM-11)
    // ============================================

    /**
     * PM-11 반복 약속 상세조회
     * @param calendId 반복 약속 ID
     * @param userId 사용자 ID
     * @return 반복 약속 상세 정보
     */
    public Map<String, Object> getDetailCycleProm(int calendId, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 반복 약속 조회
            Optional<CalendEntity> calendOpt = calendRepository.findById(calendId);

            if (calendOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "반복 약속을 찾을 수 없습니다.");
                return result;
            }

            CalendEntity calend = calendOpt.get();
            PromEntity prom = calend.getPromEntity();

            // 2. 권한 확인
            if (prom.getUsersEntity().getUser_id() != userId) {
                result.put("success", false);
                result.put("message", "반복 약속을 조회할 권한이 없습니다.");
                return result;
            }

            // 3. 반복 약속 정보 반환
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

    // ============================================
    // [12] 반복 약속 수정 (PM-12)
    // ============================================

    /**
     * PM-12 반복 약속 수정
     * @param calendDto 수정할 반복 약속 정보
     * @param userId 사용자 ID
     * @return 수정 결과
     */
    public Map<String, Object> updateCycleProm(CalendDto calendDto, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 반복 약속 조회
            Optional<CalendEntity> calendOpt = calendRepository.findById(calendDto.getCalend_id());

            if (calendOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "반복 약속을 찾을 수 없습니다.");
                return result;
            }

            CalendEntity calend = calendOpt.get();
            PromEntity prom = calend.getPromEntity();

            // 2. 권한 확인
            if (prom.getUsersEntity().getUser_id() != userId) {
                result.put("success", false);
                result.put("message", "반복 약속을 수정할 권한이 없습니다.");
                return result;
            }

            // 3. 수정 가능한 필드 업데이트
            if (calendDto.getCalend_cycle() != null) {
                calend.setCalend_cycle(calendDto.getCalend_cycle());
            }
            if (calendDto.getCalend_start() != null) {
                calend.setCalend_start(calendDto.getCalend_start());
            }
            if (calendDto.getCalend_end() != null) {
                calend.setCalend_end(calendDto.getCalend_end());
            }

            // 4. 저장
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

    // ============================================
    // [13] 반복 약속 삭제 (PM-13)
    // ============================================

    /**
     * PM-13 반복 약속 삭제
     * @param calendId 반복 약속 ID
     * @param userId 사용자 ID
     * @return 삭제 결과
     */
    public Map<String, Object> deleteCycleProm(int calendId, int userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 반복 약속 조회
            Optional<CalendEntity> calendOpt = calendRepository.findById(calendId);

            if (calendOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "반복 약속을 찾을 수 없습니다.");
                return result;
            }

            CalendEntity calend = calendOpt.get();
            PromEntity prom = calend.getPromEntity();

            // 2. 권한 확인
            if (prom.getUsersEntity().getUser_id() != userId) {
                result.put("success", false);
                result.put("message", "반복 약속을 삭제할 권한이 없습니다.");
                return result;
            }

            // 3. 삭제
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
     * @param shareToken 공유 토큰
     * @return 약속 정보
     */
    public Map<String, Object> getPromByShareToken(String shareToken) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. Share 토큰으로 조회 (Repository에 메서드 추가 필요)
            // Optional<ShareEntity> shareOpt = shareRepository.findByShareToken(shareToken);

            // 임시로 전체 조회 후 필터링 (실제로는 위의 메서드 사용)
            List<ShareEntity> allShares = shareRepository.findAll();
            Optional<ShareEntity> shareOpt = allShares.stream()
                    .filter(s -> s.getShare_token().equals(shareToken))
                    .findFirst();

            if (shareOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "유효하지 않은 공유 링크입니다.");
                return result;
            }

            ShareEntity share = shareOpt.get();
            PromEntity prom = share.getPromEntity();

            // 2. 약속 정보 반환 (민감한 정보 제외)
            Map<String, Object> promInfo = new HashMap<>();
            promInfo.put("title", prom.getProm_title());
            promInfo.put("date", prom.getProm_date());
            promInfo.put("location", prom.getProm_addr());
            promInfo.put("locationDetail", prom.getProm_addr_detail());
            promInfo.put("text", prom.getProm_text());

            result.put("success", true);
            result.put("promise", promInfo);
            result.put("shareId", share.getShare_id());

            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "약속 조회 실패: " + e.getMessage());
            return result;
        }
    }
}