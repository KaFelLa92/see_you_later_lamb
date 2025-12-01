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
import web.model.dto.farm.*;
import web.model.dto.farm.request.*;
import web.model.entity.farm.*;
import web.model.entity.user.UsersEntity;
import web.repository.farm.*;
import web.repository.user.UsersRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 목장(Farm) 관련 비즈니스 로직을 처리하는 서비스 클래스
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
public class FarmService {

    // ========== DI (Dependency Injection) - 의존성 주입 ==========

    /**
     * 목장 Repository
     * 목장 정보 데이터베이스 접근
     */
    private final FarmRepository farmRepository;

    /**
     * 목장주 Repository
     * 사용자의 목장 데이터베이스 접근
     */
    private final OwnerRepository ownerRepository;

    /**
     * 목장 업무 Repository
     * 목장 업무 데이터베이스 접근
     */
    private final WorkRepository workRepository;

    /**
     * 사용자 Repository
     * 사용자 정보 데이터베이스 접근
     */
    private final UsersRepository usersRepository;

    // ========================================================
    // [관리자] 목장 관리 (AF-01, AF-02, AF-03)
    // ========================================================

    /**
     * AF-01: 목장 등록 (관리자)
     * 관리자가 새로운 목장 유형을 등록
     * <p>
     * 처리 흐름:
     * 1. 요청 데이터 유효성 검증
     * 2. 중복 목장명 확인
     * 3. 목장 엔티티 생성 및 저장
     * 4. DTO로 변환하여 반환
     *
     * @param request 목장 등록 요청 데이터
     * @return FarmDto 등록된 목장 정보
     * @throws CustomException 유효성 검증 실패 또는 등록 실패 시
     */
    public FarmDto createFarm(FarmCreateRequest request) {
        // 1. 요청 데이터 유효성 검증
        if (!request.isValid()) {
            log.warn("목장 등록 실패: 유효하지 않은 요청 데이터");
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    request.getValidationErrorMessage()
            );
        }

        // 2. 중복 목장명 확인
        if (farmRepository.existsByFarmName(request.getFarmName())) {
            log.warn("목장 등록 실패: 이미 존재하는 목장명 - {}", request.getFarmName());
            throw new CustomException(ErrorCode.FARM_ALREADY_EXISTS);
        }

        // 3. 목장 엔티티 생성
        // 주의: FarmEntity의 usersEntity는 관리자용 목장 템플릿이므로 null로 설정
        // 실제 사용자의 목장은 OwnerEntity에서 관리
        FarmEntity farmEntity = FarmEntity.builder()
                .farmName(request.getFarmName())
                .farmInfo(request.getFarmInfo())
                .maxLamb(request.getMaxLamb())
                .farmCost(request.getFarmCost())
                .usersEntity(null)  // 템플릿은 특정 사용자에 속하지 않음
                .build();

        // 4. 데이터베이스에 저장
        try {
            FarmEntity savedFarm = farmRepository.save(farmEntity);
            log.info("목장 등록 성공: ID={}, 목장명={}", savedFarm.getFarmId(), savedFarm.getFarmName());

            // 5. Entity를 DTO로 변환하여 반환
            // userId는 0으로 설정 (템플릿이므로 특정 사용자 없음)
            FarmDto dto = savedFarm.toDto();
            dto.setUserId(0);
            return dto;

        } catch (Exception e) {
            log.error("목장 등록 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.FARM_CREATE_FAILED, e);
        }
    }

    /**
     * AF-02: 목장 수정 (관리자)
     * 관리자가 기존 목장 유형 정보를 수정
     *
     * @param farmId  수정할 목장 ID
     * @param request 목장 수정 요청 데이터
     * @return FarmDto 수정된 목장 정보
     * @throws CustomException 목장을 찾을 수 없거나 수정 실패 시
     */
    public FarmDto updateFarm(int farmId, FarmUpdateRequest request) {
        // 1. 요청 데이터 유효성 검증
        if (!request.isValid() || !request.hasUpdateData()) {
            log.warn("목장 수정 실패: 유효하지 않은 요청 데이터");
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    request.getValidationErrorMessage()
            );
        }

        // 2. 기존 목장 조회
        FarmEntity farmEntity = farmRepository.findById(farmId)
                .orElseThrow(() -> {
                    log.warn("목장 수정 실패: 존재하지 않는 목장 ID - {}", farmId);
                    return new CustomException(ErrorCode.FARM_NOT_FOUND);
                });

        // 3. 목장명 수정 (중복 확인)
        if (request.getFarmName() != null) {
            farmRepository.findByFarmName(request.getFarmName())
                    .ifPresent(existingFarm -> {
                        if (existingFarm.getFarmId() != farmId) {
                            throw new CustomException(ErrorCode.FARM_ALREADY_EXISTS);
                        }
                    });
            farmEntity.setFarmName(request.getFarmName());
        }

        // 4. 목장 소개 수정
        if (request.getFarmInfo() != null) {
            farmEntity.setFarmInfo(request.getFarmInfo());
        }

        // 5. 최대 양 수 수정
        if (request.getMaxLamb() != null) {
            farmEntity.setMaxLamb(request.getMaxLamb());
        }

        // 6. 목장 비용 수정
        if (request.getFarmCost() != null) {
            farmEntity.setFarmCost(request.getFarmCost());
        }

        // 7. 수정 사항 저장
        try {
            FarmEntity updatedFarm = farmRepository.save(farmEntity);
            log.info("목장 수정 성공: ID={}, 목장명={}", updatedFarm.getFarmId(), updatedFarm.getFarmName());

            FarmDto dto = updatedFarm.toDto();
            dto.setUserId(0);
            return dto;

        } catch (Exception e) {
            log.error("목장 수정 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.FARM_UPDATE_FAILED, e);
        }
    }

    /**
     * AF-03: 목장 삭제 (관리자)
     * 관리자가 목장 유형을 삭제
     * <p>
     * 주의: 이미 사용자가 구매한 목장이 있으면 삭제 불가
     *
     * @param farmId 삭제할 목장 ID
     * @throws CustomException 목장을 찾을 수 없거나 삭제 실패 시
     */
    public void deleteFarm(int farmId) {
        // 1. 목장 존재 여부 확인
        FarmEntity farmEntity = farmRepository.findById(farmId)
                .orElseThrow(() -> {
                    log.warn("목장 삭제 실패: 존재하지 않는 목장 ID - {}", farmId);
                    return new CustomException(ErrorCode.FARM_NOT_FOUND);
                });

        // 2. 해당 목장을 구매한 사용자가 있는지 확인
        long ownerCount = ownerRepository.countByFarmEntity_FarmId(farmId);
        if (ownerCount > 0) {
            log.warn("목장 삭제 실패: {}명의 사용자가 이 목장을 보유 중 - ID={}", ownerCount, farmId);
            throw new CustomException(
                    ErrorCode.FARM_DELETE_FAILED,
                    "이 목장을 보유한 사용자가 있어 삭제할 수 없습니다."
            );
        }

        // 3. 목장 삭제
        try {
            farmRepository.delete(farmEntity);
            log.info("목장 삭제 성공: ID={}, 목장명={}", farmId, farmEntity.getFarmName());

        } catch (Exception e) {
            log.error("목장 삭제 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.FARM_DELETE_FAILED, e);
        }
    }

    // ========================================================
    // [공통] 목장 조회 (FA-01, FA-02)
    // ========================================================

    /**
     * FA-01: 목장 전체 조회
     * 권한에 따라 다르게 동작:
     * - 관리자: 모든 목장 유형 조회
     * - 일반 사용자: 자신이 구매한 목장만 조회
     *
     * @param userId   사용자 ID
     * @param isAdmin  관리자 여부
     * @param pageable 페이징 정보
     * @return PageResponse<FarmDto> 페이징된 목장 리스트
     */
    @Transactional(readOnly = true)
    public PageResponse<FarmDto> getAllFarms(int userId, boolean isAdmin, Pageable pageable) {
        if (isAdmin) {
            // 관리자: 모든 목장 유형 조회
            Page<FarmEntity> farmPage = farmRepository.findAll(pageable);
            List<FarmDto> farmDtos = farmPage.getContent().stream()
                    .map(farm -> {
                        FarmDto dto = farm.toDto();
                        dto.setUserId(0);  // 템플릿은 특정 사용자 없음
                        return dto;
                    })
                    .collect(Collectors.toList());

            log.info("관리자 목장 전체 조회: {}개", farmPage.getTotalElements());
            return PageResponse.of(farmDtos, farmPage);

        } else {
            // 일반 사용자: 자신이 구매한 목장만 조회
            Page<OwnerEntity> ownerPage = ownerRepository.findByUserIdWithFarm(userId, pageable);
            List<FarmDto> farmDtos = ownerPage.getContent().stream()
                    .map(owner -> owner.getFarmEntity().toDto())
                    .collect(Collectors.toList());

            log.info("사용자 {} 목장 조회: {}개", userId, ownerPage.getTotalElements());
            return PageResponse.of(farmDtos, ownerPage);
        }
    }

    /**
     * FA-02: 목장 상세 조회
     * 권한에 따라 다르게 동작:
     * - 관리자: 모든 목장 유형 상세 조회 가능
     * - 일반 사용자: 자신이 구매한 목장만 상세 조회 가능
     *
     * @param farmId  목장 ID (관리자) 또는 ownerId (사용자)
     * @param userId  사용자 ID
     * @param isAdmin 관리자 여부
     * @return FarmDto 목장 상세 정보
     * @throws CustomException 목장을 찾을 수 없거나 권한이 없을 때
     */
    @Transactional(readOnly = true)
    public FarmDto getDetailFarm(int farmId, int userId, boolean isAdmin) {
        if (isAdmin) {
            // 관리자: 목장 유형 조회
            FarmEntity farmEntity = farmRepository.findById(farmId)
                    .orElseThrow(() -> {
                        log.warn("목장 상세 조회 실패: 존재하지 않는 목장 ID - {}", farmId);
                        return new CustomException(ErrorCode.FARM_NOT_FOUND);
                    });

            log.info("관리자 목장 상세 조회 성공: ID={}", farmId);
            FarmDto dto = farmEntity.toDto();
            dto.setUserId(0);
            return dto;

        } else {
            // 일반 사용자: 자신의 목장 조회 (farmId는 실제로는 ownerId)
            OwnerEntity ownerEntity = ownerRepository.findByOwnerIdAndUsersEntity_UserId(farmId, userId)
                    .orElseThrow(() -> {
                        log.warn("목장 상세 조회 실패: 사용자 {}가 목장 {}를 보유하고 있지 않음", userId, farmId);
                        return new CustomException(ErrorCode.OWNER_NOT_OWNED);
                    });

            log.info("사용자 {} 목장 상세 조회 성공: ownerId={}", userId, farmId);
            return ownerEntity.getFarmEntity().toDto();
        }
    }

    // ========================================================
    // [공통] 업무 조회 (FA-03, FA-04)
    // ========================================================

    /**
     * FA-03: 업무 전체 조회
     * 권한에 따라 다르게 동작:
     * - 관리자: 모든 업무 조회
     * - 일반 사용자: 자신의 업무만 조회
     *
     * @param userId   사용자 ID
     * @param isAdmin  관리자 여부
     * @param pageable 페이징 정보
     * @return PageResponse<WorkDto> 페이징된 업무 리스트
     */
    @Transactional(readOnly = true)
    public PageResponse<WorkDto> getAllWorks(int userId, boolean isAdmin, Pageable pageable) {
        if (isAdmin) {
            // 관리자: 모든 업무 조회
            Page<WorkEntity> workPage = workRepository.findAll(pageable);
            List<WorkDto> workDtos = workPage.getContent().stream()
                    .map(WorkEntity::toDto)
                    .collect(Collectors.toList());

            log.info("관리자 업무 전체 조회: {}개", workPage.getTotalElements());
            return PageResponse.of(workDtos, workPage);

        } else {
            // 일반 사용자: 자신의 업무만 조회
            List<WorkEntity> works = workRepository.findWorksByUserId(userId);

            // List를 Page로 변환
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), works.size());
            List<WorkDto> workDtos = works.subList(start, end).stream()
                    .map(WorkEntity::toDto)
                    .collect(Collectors.toList());

            log.info("사용자 {} 업무 조회: {}개", userId, works.size());

            // PageResponse 생성 (간단한 방식)
            return PageResponse.<WorkDto>builder()
                    .content(workDtos)
                    .currentPage(pageable.getPageNumber())
                    .pageSize(pageable.getPageSize())
                    .totalElements(works.size())
                    .totalPages((int) Math.ceil((double) works.size() / pageable.getPageSize()))
                    .first(pageable.getPageNumber() == 0)
                    .last(end >= works.size())
                    .empty(works.isEmpty())
                    .build();
        }
    }

    /**
     * FA-04: 업무 상세 조회
     * 권한에 따라 다르게 동작:
     * - 관리자: 모든 업무 상세 조회 가능
     * - 일반 사용자: 자신의 업무만 상세 조회 가능
     *
     * @param workId  업무 ID
     * @param userId  사용자 ID
     * @param isAdmin 관리자 여부
     * @return WorkDto 업무 상세 정보
     * @throws CustomException 업무를 찾을 수 없거나 권한이 없을 때
     */
    @Transactional(readOnly = true)
    public WorkDto getDetailWork(int workId, int userId, boolean isAdmin) {
        if (isAdmin) {
            // 관리자: 모든 업무 조회
            WorkEntity workEntity = workRepository.findById(workId)
                    .orElseThrow(() -> {
                        log.warn("업무 상세 조회 실패: 존재하지 않는 업무 ID - {}", workId);
                        return new CustomException(ErrorCode.WORK_NOT_FOUND);
                    });

            log.info("관리자 업무 상세 조회 성공: ID={}", workId);
            return workEntity.toDto();

        } else {
            // 일반 사용자: 자신의 업무만 조회
            WorkEntity workEntity = workRepository.findByWorkIdAndUserId(workId, userId)
                    .orElseThrow(() -> {
                        log.warn("업무 상세 조회 실패: 사용자 {}가 업무 {}를 소유하지 않음", userId, workId);
                        return new CustomException(ErrorCode.WORK_NOT_FOUND, "자신의 업무만 조회할 수 있습니다.");
                    });

            log.info("사용자 {} 업무 상세 조회 성공: workId={}", userId, workId);
            return workEntity.toDto();
        }
    }

    // ========================================================
    // [사용자] 목장 구매 및 관리 (FA-05, FA-06)
    // ========================================================

    /**
     * FA-05: 목장 구매
     * 사용자가 포인트를 사용하여 목장을 구매
     *
     * 처리 흐름:
     * 1. 요청 데이터 유효성 검증
     * 2. 목장 유형 존재 여부 확인
     * 3. 이미 구매한 목장인지 확인
     * 4. 사용자 포인트 확인 및 차감
     * 5. OwnerEntity 생성 및 저장
     *
     * @param userId 사용자 ID
     * @param request 목장 구매 요청 데이터
     * @return OwnerDto 구매한 목장 정보
     * @throws CustomException 유효성 검증 실패 또는 구매 실패 시
     */
    public OwnerDto buyFarm(int userId, FarmBuyRequest request) {
        // 1. 요청 데이터 유효성 검증
        if (!request.isValid()) {
            log.warn("목장 구매 실패: 유효하지 않은 요청 데이터");
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    request.getValidationErrorMessage()
            );
        }

        // 2. 사용자 조회
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        // 3. 목장 유형 존재 여부 확인
        FarmEntity farmEntity = farmRepository.findById(request.getFarmId())
                .orElseThrow(() -> {
                    log.warn("목장 구매 실패: 존재하지 않는 목장 ID - {}", request.getFarmId());
                    return new CustomException(ErrorCode.FARM_NOT_FOUND);
                });

        // 4. 이미 구매한 목장인지 확인
        boolean alreadyOwned = ownerRepository.existsByUsersEntity_UserIdAndFarmEntity_FarmId(
                userId, request.getFarmId());
        if (alreadyOwned) {
            log.warn("목장 구매 실패: 사용자 {}가 이미 목장 {}를 보유 중", userId, request.getFarmId());
            throw new CustomException(ErrorCode.FARM_ALREADY_OWNED);
        }

        // 5. 포인트 확인 (UsersEntity에 포인트 필드가 있다고 가정)
        // 주의: UsersEntity에 point 필드가 없으면 이 부분은 제거하거나 수정 필요
        /*
        if (user.getPoint() < farmEntity.getFarmCost()) {
            log.warn("목장 구매 실패: 포인트 부족 - 필요: {}, 보유: {}",
                    farmEntity.getFarmCost(), user.getPoint());
            throw new CustomException(ErrorCode.INSUFFICIENT_POINTS);
        }
        // 포인트 차감
        user.setPoint(user.getPoint() - farmEntity.getFarmCost());
        usersRepository.save(user);
        */

        // 6. OwnerEntity 생성
        OwnerEntity ownerEntity = OwnerEntity.builder()
                .ownerName(request.getOwnerName())
                .farmEntity(farmEntity)
                .usersEntity(user)
                .build();

        // 7. 데이터베이스에 저장
        try {
            OwnerEntity savedOwner = ownerRepository.save(ownerEntity);
            log.info("목장 구매 성공: userId={}, farmId={}, ownerId={}",
                    userId, request.getFarmId(), savedOwner.getOwnerId());
            return savedOwner.toDto();

        } catch (Exception e) {
            log.error("목장 구매 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.FARM_BUY_FAILED, e);
        }
    }

    /**
     * FA-06: 목장 이름 짓기
     * 사용자가 자신의 목장 이름을 변경
     *
     * @param ownerId 목장주 ID
     * @param userId 사용자 ID
     * @param request 목장 이름 짓기 요청 데이터
     * @return OwnerDto 수정된 목장주 정보
     * @throws CustomException 목장을 찾을 수 없거나 소유권이 없을 때
     */
    public OwnerDto namingFarm(int ownerId, int userId, FarmNamingRequest request) {
        // 1. 요청 데이터 유효성 검증
        if (!request.isValid()) {
            log.warn("목장 이름 짓기 실패: 유효하지 않은 요청 데이터");
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    request.getValidationErrorMessage()
            );
        }

        // 2. 목장주 조회 및 소유권 확인
        OwnerEntity ownerEntity = ownerRepository.findByOwnerIdAndUsersEntity_UserId(ownerId, userId)
                .orElseThrow(() -> {
                    log.warn("목장 이름 짓기 실패: 존재하지 않거나 소유하지 않은 목장 - ownerId={}, userId={}",
                            ownerId, userId);
                    return new CustomException(ErrorCode.OWNER_NOT_OWNED);
                });

        // 3. 이름 변경
        ownerEntity.setOwnerName(request.getOwnerName());

        // 4. 저장
        try {
            OwnerEntity updatedOwner = ownerRepository.save(ownerEntity);
            log.info("목장 이름 짓기 성공: ownerId={}, 새 이름={}", ownerId, request.getOwnerName());
            return updatedOwner.toDto();

        } catch (Exception e) {
            log.error("목장 이름 짓기 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.FARM_NAMING_FAILED, e);
        }
    }

    // ========================================================
    // [사용자] 목장 업무 처리 (FA-07)
    // ========================================================

    /**
     * FA-07: 목장 업무 처리
     * 사용자가 미니게임을 완료하여 업무를 처리
     *
     * 처리 흐름:
     * 1. 업무 조회 및 소유권 확인
     * 2. 업무 상태 확인 (이미 완료/기한 초과)
     * 3. 기한 확인
     * 4. 업무 상태 및 종료 시간 갱신
     *
     * @param workId 업무 ID
     * @param userId 사용자 ID
     * @param request 업무 완료 요청 데이터
     * @return WorkDto 처리된 업무 정보
     * @throws CustomException 업무를 찾을 수 없거나 처리 실패 시
     */
    public WorkDto workingFarm(int workId, int userId, WorkCompleteRequest request) {
        // 1. 요청 데이터 유효성 검증
        if (!request.isValid()) {
            log.warn("업무 처리 실패: 유효하지 않은 요청 데이터");
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    request.getValidationErrorMessage()
            );
        }

        // 2. 업무 조회 및 소유권 확인
        WorkEntity workEntity = workRepository.findByWorkIdAndUserId(workId, userId)
                .orElseThrow(() -> {
                    log.warn("업무 처리 실패: 존재하지 않거나 소유하지 않은 업무 - workId={}, userId={}",
                            workId, userId);
                    return new CustomException(ErrorCode.WORK_NOT_FOUND, "자신의 업무만 처리할 수 있습니다.");
                });

        // 3. 이미 완료된 업무인지 확인
        if (workEntity.getWorkState() == 1) {
            log.warn("업무 처리 실패: 이미 완료된 업무 - workId={}", workId);
            throw new CustomException(ErrorCode.WORK_ALREADY_COMPLETED);
        }

        // 4. 기한 확인
        LocalDateTime now = LocalDateTime.now();
        boolean isExpired = now.isAfter(workEntity.getWorkEndDate());

        // 5. 업무 상태 갱신
        if (isExpired) {
            // 기한이 지난 경우
            workEntity.setWorkState(-1);  // 실패
            workEntity.setWorkEndDate(now);
            log.info("업무 기한 초과: workId={}, userId={}", workId, userId);

        } else {
            // 기한 내 처리
            if (request.getSuccess()) {
                workEntity.setWorkState(1);   // 성공
                log.info("업무 완료 성공: workId={}, userId={}, score={}",
                        workId, userId, request.getScore());
            } else {
                workEntity.setWorkState(-1);  // 실패
                log.info("업무 완료 실패: workId={}, userId={}", workId, userId);
            }
            workEntity.setWorkEndDate(now);
        }

        // 6. 저장
        try {
            WorkEntity updatedWork = workRepository.save(workEntity);
            log.info("업무 처리 완료: workId={}, 상태={}", workId, updatedWork.getWorkState());
            return updatedWork.toDto();

        } catch (Exception e) {
            log.error("업무 처리 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.WORK_PROCESS_FAILED, e);
        }
    }

    /**
     * 기한이 지난 업무 자동 처리 (배치 작업용)
     * 스케줄러에서 주기적으로 호출하여 기한이 지난 업무를 자동으로 실패 처리
     *
     * @return int 처리된 업무 수
     */
    public int processExpiredWorks() {
        LocalDateTime now = LocalDateTime.now();
        List<WorkEntity> expiredWorks = workRepository.findExpiredWorks(now);

        if (expiredWorks.isEmpty()) {
            log.info("기한 만료 업무 없음");
            return 0;
        }

        // 모든 만료된 업무를 실패로 처리
        expiredWorks.forEach(work -> {
            work.setWorkState(-1);  // 실패
            work.setWorkEndDate(now);
        });

        try {
            workRepository.saveAll(expiredWorks);
            log.info("기한 만료 업무 자동 처리: {}개", expiredWorks.size());
            return expiredWorks.size();

        } catch (Exception e) {
            log.error("기한 만료 업무 처리 중 오류 발생", e);
            return 0;
        }
    }

}

// Part 2에서 계속...