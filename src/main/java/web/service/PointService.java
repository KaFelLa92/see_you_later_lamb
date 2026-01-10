package web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.exception.CustomException;
import web.exception.ErrorCode;
import web.model.dto.common.PageResponse;
import web.model.dto.point.*;
import web.model.dto.point.request.*;
import web.model.entity.farm.FarmEntity;
import web.model.entity.farm.WorkEntity;
import web.model.entity.point.*;
import web.model.entity.promise.ShareEntity;
import web.model.entity.user.AtenEntity;
import web.model.entity.user.UsersEntity;
import web.repository.farm.FarmRepository;
import web.repository.farm.WorkRepository;
import web.repository.point.*;
import web.repository.promise.ShareRepository;
import web.repository.user.AtenRepository;
import web.repository.user.UsersRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 포인트(Point) 관련 비즈니스 로직을 처리하는 서비스 클래스
 *
 * @Slf4j: Lombok 로깅 어노테이션
 * @Service: Spring Service 계층 컴포넌트
 * @RequiredArgsConstructor: final 필드 생성자 자동 생성 (DI용)
 * @Transactional: 클래스 레벨 트랜잭션 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PointService {

    // ========== DI (Dependency Injection) - 의존성 주입 ==========

    /**
     * 포인트 정책 Repository
     */
    private final PointRepository pointRepository;

    /**
     * 포인트 지급 Repository
     */
    private final PayRepository payRepository;

    /**
     * 사용자 Repository
     */
    private final UsersRepository usersRepository;

    /**
     * 출석 Repository
     */
    private final AtenRepository atenRepository;

    /**
     * 약속 공유 Repository
     */
    private final ShareRepository shareRepository;

    /**
     * 목장 업무 Repository
     */
    private final WorkRepository workRepository;

    /**
     * 목장 Repository
     */
    private final FarmRepository farmRepository;

    // ========================================================
    // [관리자] 포인트 정책 관리 (AP-01, AP-02)
    // ========================================================

    /**
     * AP-01: 포인트 정책 수정 (관리자)
     * 관리자가 포인트 정책을 수정
     *
     * @param pointId 포인트 정책 ID
     * @param request 포인트 정책 수정 요청 데이터
     * @return PointDto 수정된 포인트 정책 정보
     * @throws CustomException 정책을 찾을 수 없거나 수정 실패 시
     */
    public PointDto updatePointPolicy(int pointId, PointPolicyUpdateRequest request) {
        // 1. 요청 데이터 유효성 검증
        if (!request.isValid() || !request.hasUpdateData()) {
            log.warn("포인트 정책 수정 실패: 유효하지 않은 요청 데이터");
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    request.getValidationErrorMessage()
            );
        }

        // 2. 기존 포인트 정책 조회
        PointEntity pointEntity = pointRepository.findById(pointId)
                .orElseThrow(() -> {
                    log.warn("포인트 정책 수정 실패: 존재하지 않는 정책 ID - {}", pointId);
                    return new CustomException(ErrorCode.POINT_POLICY_NOT_FOUND);
                });

        // 3. 포인트 정책명 수정
        if (request.getPointName() != null) {
            // 다른 정책이 이미 사용 중인 이름인지 확인
            pointRepository.findByPointName(request.getPointName())
                    .ifPresent(existingPolicy -> {
                        if (existingPolicy.getPointId() != pointId) {
                            throw new CustomException(
                                    ErrorCode.POINT_POLICY_UPDATE_FAILED,
                                    "이미 존재하는 정책명입니다."
                            );
                        }
                    });
            pointEntity.setPointName(request.getPointName());
        }

        // 4. 지급 포인트 수정
        if (request.getUpdatePoint() != null) {
            pointEntity.setUpdatePoint(request.getUpdatePoint());
        }

        // 5. 수정 사항 저장
        try {
            PointEntity updatedPolicy = pointRepository.save(pointEntity);
            log.info("포인트 정책 수정 성공: ID={}, 정책명={}, 포인트={}",
                    updatedPolicy.getPointId(),
                    updatedPolicy.getPointName(),
                    updatedPolicy.getUpdatePoint());
            return updatedPolicy.toDto();

        } catch (Exception e) {
            log.error("포인트 정책 수정 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.POINT_POLICY_UPDATE_FAILED, e);
        }
    }

    /**
     * AP-02: 포인트 정책 삭제 (관리자)
     * 관리자가 포인트 정책을 삭제
     *
     * 주의: 이미 사용된 정책은 삭제 불가
     *
     * @param pointId 포인트 정책 ID
     * @throws CustomException 정책을 찾을 수 없거나 삭제 실패 시
     */
    public void deletePointPolicy(int pointId) {
        // 1. 포인트 정책 존재 여부 확인
        PointEntity pointEntity = pointRepository.findById(pointId)
                .orElseThrow(() -> {
                    log.warn("포인트 정책 삭제 실패: 존재하지 않는 정책 ID - {}", pointId);
                    return new CustomException(ErrorCode.POINT_POLICY_NOT_FOUND);
                });

        // 2. 해당 정책으로 지급된 내역이 있는지 확인
        long payCount = payRepository.countByPointEntity_PointId(pointId);
        if (payCount > 0) {
            log.warn("포인트 정책 삭제 실패: {}건의 지급 내역이 있음 - ID={}", payCount, pointId);
            throw new CustomException(
                    ErrorCode.POINT_POLICY_DELETE_FAILED,
                    "이 정책으로 지급된 내역이 있어 삭제할 수 없습니다."
            );
        }

        // 3. 포인트 정책 삭제
        try {
            pointRepository.delete(pointEntity);
            log.info("포인트 정책 삭제 성공: ID={}, 정책명={}", pointId, pointEntity.getPointName());

        } catch (Exception e) {
            log.error("포인트 정책 삭제 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.POINT_POLICY_DELETE_FAILED, e);
        }
    }

    // ========================================================
    // [공통] 포인트 정책 조회 (AP-03, AP-04)
    // ========================================================

    /**
     * AP-03: 포인트 정책 전체 조회
     *
     * @param pageable 페이징 정보
     * @return PageResponse<PointDto> 페이징된 포인트 정책 리스트
     */
    @Transactional(readOnly = true)
    public PageResponse<PointDto> getAllPointPolicies(Pageable pageable) {
        Page<PointEntity> pointPage = pointRepository.findAll(pageable);
        List<PointDto> pointDtos = pointPage.getContent().stream()
                .map(PointEntity::toDto)
                .collect(Collectors.toList());

        log.info("포인트 정책 전체 조회: {}개", pointPage.getTotalElements());
        return PageResponse.of(pointDtos, pointPage);
    }

    /**
     * AP-04: 포인트 정책 상세 조회
     *
     * @param pointId 포인트 정책 ID
     * @return PointDto 포인트 정책 상세 정보
     * @throws CustomException 정책을 찾을 수 없을 때
     */
    @Transactional(readOnly = true)
    public PointDto getDetailPointPolicy(int pointId) {
        PointEntity pointEntity = pointRepository.findById(pointId)
                .orElseThrow(() -> {
                    log.warn("포인트 정책 상세 조회 실패: 존재하지 않는 정책 ID - {}", pointId);
                    return new CustomException(ErrorCode.POINT_POLICY_NOT_FOUND);
                });

        log.info("포인트 정책 상세 조회 성공: ID={}", pointId);
        return pointEntity.toDto();
    }

    // ========================================================
    // [공통] 포인트 지급/차감 (AP-05)
    // ========================================================

    /**
     * AP-05: 포인트 지급/차감
     * 사용자에게 포인트를 지급하거나 차감
     *
     * 처리 흐름:
     * 1. 요청 데이터 유효성 검증
     * 2. 사용자 조회
     * 3. 포인트 정책 조회
     * 4. 중복 지급 확인 (출석, 약속, 업무)
     * 5. 포인트 차감 시 잔액 확인
     * 6. 사용자 포인트 업데이트
     * 7. PayEntity 생성 및 저장
     *
     * @param request 포인트 지급/차감 요청 데이터
     * @return PayDto 포인트 지급 내역
     * @throws CustomException 유효성 검증 실패 또는 지급 실패 시
     */
    public PayDto createPoint(PointPayRequest request) {
        // 1. 요청 데이터 유효성 검증
        if (!request.isValid()) {
            log.warn("포인트 지급/차감 실패: 유효하지 않은 요청 데이터");
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    request.getValidationErrorMessage()
            );
        }

        // 2. 사용자 조회
        UsersEntity user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.warn("포인트 지급/차감 실패: 존재하지 않는 사용자 - userId={}", request.getUserId());
                    return new CustomException(ErrorCode.UNAUTHORIZED, "사용자를 찾을 수 없습니다.");
                });

        // 3. 포인트 정책 조회
        PointEntity pointPolicy = pointRepository.findById(request.getPointPolicyId())
                .orElseThrow(() -> {
                    log.warn("포인트 지급/차감 실패: 존재하지 않는 정책 - policyId={}", request.getPointPolicyId());
                    return new CustomException(ErrorCode.POINT_POLICY_NOT_FOUND);
                });

        // 4. 중복 지급 확인
        checkDuplicatePay(request);

        // 5. 연관 엔티티 조회 (optional)
        AtenEntity atenEntity = request.getAtenId() != null
                ? atenRepository.findById(request.getAtenId()).orElse(null)
                : null;

        ShareEntity shareEntity = request.getShareId() != null
                ? shareRepository.findById(request.getShareId()).orElse(null)
                : null;

        WorkEntity workEntity = request.getWorkId() != null
                ? workRepository.findById(request.getWorkId()).orElse(null)
                : null;

        FarmEntity farmEntity = request.getFarmId() != null
                ? farmRepository.findById(request.getFarmId()).orElse(null)
                : null;

        // 6. 포인트 차감 시 잔액 확인
        int pointChange = pointPolicy.getUpdatePoint();
        if (pointChange < 0 && user.getPoint() < Math.abs(pointChange)) {
            log.warn("포인트 차감 실패: 포인트 부족 - userId={}, 필요: {}, 보유: {}",
                    user.getUserId(), Math.abs(pointChange), user.getPoint());
            throw new CustomException(ErrorCode.INSUFFICIENT_POINTS);
        }

        // 7. 사용자 포인트 업데이트
        user.setPoint(user.getPoint() + pointChange);
        usersRepository.save(user);

        // 8. PayEntity 생성
        PayEntity payEntity = PayEntity.builder()
                .atenEntity(atenEntity)
                .shareEntity(shareEntity)
                .workEntity(workEntity)
                .farmEntity(farmEntity)
                .pointEntity(pointPolicy)
                .build();

        // 9. 데이터베이스에 저장
        try {
            PayEntity savedPay = payRepository.save(payEntity);
            log.info("포인트 {}성공: userId={}, 정책={}, 포인트={}, 활동={}",
                    pointChange > 0 ? "지급 " : "차감 ",
                    user.getUserId(),
                    pointPolicy.getPointName(),
                    pointChange,
                    request.getActivityType());

            return savedPay.toDto();

        } catch (Exception e) {
            log.error("포인트 지급/차감 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.POINT_PAY_FAILED, e);
        }
    }

    /**
     * 중복 지급 확인
     * 출석, 약속, 업무에 대해서는 중복 지급 방지
     *
     * @param request 포인트 지급 요청 데이터
     * @throws CustomException 이미 지급된 경우
     */
    private void checkDuplicatePay(PointPayRequest request) {
        // 출석 중복 확인
        if (request.getAtenId() != null) {
            if (payRepository.existsByAtenEntity_AtenId(request.getAtenId())) {
                log.warn("포인트 중복 지급: 이미 지급된 출석 - atenId={}", request.getAtenId());
                throw new CustomException(ErrorCode.POINT_ALREADY_PAID, "이미 출석 포인트가 지급되었습니다.");
            }
        }

        // 약속 공유 중복 확인
        if (request.getShareId() != null) {
            if (payRepository.existsByShareEntity_ShareId(request.getShareId())) {
                log.warn("포인트 중복 지급: 이미 지급된 약속 - shareId={}", request.getShareId());
                throw new CustomException(ErrorCode.POINT_ALREADY_PAID, "이미 약속 이행 포인트가 지급되었습니다.");
            }
        }

        // 목장 업무 중복 확인
        if (request.getWorkId() != null) {
            if (payRepository.existsByWorkEntity_WorkId(request.getWorkId())) {
                log.warn("포인트 중복 지급: 이미 지급된 업무 - workId={}", request.getWorkId());
                throw new CustomException(ErrorCode.POINT_ALREADY_PAID, "이미 업무 완료 포인트가 지급되었습니다.");
            }
        }
    }
}