package web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import web.exception.CustomException;
import web.exception.ErrorCode;
import web.model.dto.common.PageResponse;
import web.model.dto.lamb.*;
import web.model.dto.lamb.request.*;
import web.model.entity.common.LambRank;
import web.model.entity.lamb.*;
import web.model.entity.promise.ShareEntity;
import web.model.entity.user.UsersEntity;
import web.repository.lamb.*;
import web.repository.promise.ShareRepository;
import web.repository.user.UsersRepository;
import web.util.FileUploadUtil;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 양(Lamb) 관련 비즈니스 로직을 처리하는 서비스 클래스
 *
 * @Slf4j: Lombok 로깅 어노테이션
 * @Service: Spring Service 계층 컴포넌트
 * @RequiredArgsConstructor: final 필드 생성자 자동 생성 (DI용)
 * @Transactional: 클래스 레벨 트랜잭션 처리
 *   - readOnly=false가 기본값 (CUD 작업 가능)
 *   - 메서드 실행 중 예외 발생 시 자동 롤백
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LambService {

    // ========== DI (Dependency Injection) - 의존성 주입 ==========

    /**
     * 양 Repository
     * 양 정보 데이터베이스 접근
     */
    private final LambRepository lambRepository;

    /**
     * 양 특성 Repository
     * 양 특성 데이터베이스 접근
     */
    private final LambCharRepository lambCharRepository;

    /**
     * 확률 Repository
     * 등장 확률 데이터베이스 접근
     */
    private final ProbRepository probRepository;

    /**
     * 양치기 Repository
     * 사용자의 양 데이터베이스 접근
     */
    private final ShepRepository shepRepository;

    /**
     * 사용자 Repository
     * 사용자 정보 데이터베이스 접근
     */
    private final UsersRepository usersRepository;

    /**
     * 약속 공유 Repository
     * 약속 공유 정보 데이터베이스 접근
     */
    private final ShareRepository shareRepository;

    /**
     * 파일 업로드 유틸리티
     * 양 일러스트 이미지 업로드 처리
     */
    private final FileUploadUtil fileUploadUtil;

    /**
     * 난수 생성기
     * 양 등장, 등급 결정 등에 사용
     */
    private final Random random = new Random();

    // ========================================================
    // [관리자] 양 품종 관리 (AL-01, AL-02, AL-03)
    // ========================================================

    /**
     * AL-01: 양 등록 (관리자)
     * 관리자가 새로운 양 품종을 등록
     *
     * 처리 흐름:
     * 1. 요청 데이터 유효성 검증
     * 2. 중복 품종명 확인
     * 3. 양 특성 존재 여부 확인
     * 4. 양 엔티티 생성 및 저장
     * 5. DTO로 변환하여 반환
     *
     * @param request 양 등록 요청 데이터
     * @param imageFile 양 일러스트 이미지 파일 (선택)
     * @return LambDto 등록된 양 정보
     * @throws CustomException 유효성 검증 실패 또는 등록 실패 시
     */
    public LambDto createLamb(LambCreateRequest request, MultipartFile imageFile) {
        // 1. 요청 데이터 유효성 검증
        if (!request.isValid()) {
            log.warn("양 등록 실패: 유효하지 않은 요청 데이터");
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    request.getValidationErrorMessage()
            );
        }

        // 2. 중복 품종명 확인
        if (lambRepository.existsByLambName(request.getLambName())) {
            log.warn("양 등록 실패: 이미 존재하는 품종명 - {}", request.getLambName());
            throw new CustomException(ErrorCode.LAMB_ALREADY_EXISTS);
        }

        // 3. 양 특성 존재 여부 확인
        LambCharEntity lambCharEntity = lambCharRepository.findById(request.getCharId())
                .orElseThrow(() -> {
                    log.warn("양 등록 실패: 존재하지 않는 양 특성 ID - {}", request.getCharId());
                    return new CustomException(ErrorCode.LAMB_CHAR_NOT_FOUND);
                });

        // 4. 이미지 파일 업로드 처리 (선택적)
        String lambPath = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // 파일 업로드 (lamb 디렉토리에 저장)
                lambPath = fileUploadUtil.uploadFile(imageFile, "lamb");
                log.info("양 이미지 업로드 성공: {}", lambPath);
            } catch (Exception e) {
                log.error("양 이미지 업로드 실패", e);
                throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED, e);
            }
        }

        // 5. 양 엔티티 생성
        LambEntity lambEntity = LambEntity.builder()
                .lambName(request.getLambName())
                .lambInfo(request.getLambInfo())
                .lambRank(request.getLambRank())
                .lambPath(lambPath)
                .lambCharEntity(lambCharEntity)
                .build();

        // 6. 데이터베이스에 저장
        try {
            LambEntity savedLamb = lambRepository.save(lambEntity);
            log.info("양 등록 성공: ID={}, 품종명={}", savedLamb.getLambId(), savedLamb.getLambName());

            // 7. Entity를 DTO로 변환하여 반환
            return savedLamb.toDto();

        } catch (Exception e) {
            log.error("양 등록 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.LAMB_CREATE_FAILED, e);
        }
    }

    /**
     * AL-02: 양 수정 (관리자)
     * 관리자가 기존 양 품종 정보를 수정
     *
     * @param lambId 수정할 양 ID
     * @param request 양 수정 요청 데이터
     * @param imageFile 새 일러스트 이미지 파일 (선택)
     * @return LambDto 수정된 양 정보
     * @throws CustomException 양을 찾을 수 없거나 수정 실패 시
     */
    public LambDto updateLamb(int lambId, LambUpdateRequest request, MultipartFile imageFile) {
        // 1. 요청 데이터 유효성 검증
        if (!request.isValid() || !request.hasUpdateData()) {
            log.warn("양 수정 실패: 유효하지 않은 요청 데이터");
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    request.getValidationErrorMessage()
            );
        }

        // 2. 기존 양 조회
        LambEntity lambEntity = lambRepository.findById(lambId)
                .orElseThrow(() -> {
                    log.warn("양 수정 실패: 존재하지 않는 양 ID - {}", lambId);
                    return new CustomException(ErrorCode.LAMB_NOT_FOUND);
                });

        // 3. 품종명 수정 (중복 확인)
        if (request.getLambName() != null) {
            // 다른 양이 이미 사용 중인 품종명인지 확인
            lambRepository.findByLambName(request.getLambName())
                    .ifPresent(existingLamb -> {
                        // 자기 자신이 아니면서 같은 이름을 가진 양이 있으면 예외
                        if (existingLamb.getLambId() != lambId) {
                            throw new CustomException(ErrorCode.LAMB_ALREADY_EXISTS);
                        }
                    });
            lambEntity.setLambName(request.getLambName());
        }

        // 4. 양 소개 수정
        if (request.getLambInfo() != null) {
            lambEntity.setLambInfo(request.getLambInfo());
        }

        // 5. 양 등급 수정
        if (request.getLambRank() != null) {
            lambEntity.setLambRank(request.getLambRank());
        }

        // 6. 양 특성 수정
        if (request.getCharId() != null) {
            LambCharEntity lambCharEntity = lambCharRepository.findById(request.getCharId())
                    .orElseThrow(() -> new CustomException(ErrorCode.LAMB_CHAR_NOT_FOUND));
            lambEntity.setLambCharEntity(lambCharEntity);
        }

        // 7. 이미지 파일 수정 (선택적)
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // 기존 이미지 삭제 (있는 경우)
                if (lambEntity.getLambPath() != null) {
                    fileUploadUtil.deleteFile(lambEntity.getLambPath());
                }
                // 새 이미지 업로드
                String newLambPath = fileUploadUtil.uploadFile(imageFile, "lamb");
                lambEntity.setLambPath(newLambPath);
                log.info("양 이미지 수정 성공: {}", newLambPath);
            } catch (Exception e) {
                log.error("양 이미지 업로드 실패", e);
                throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED, e);
            }
        }

        // 8. 수정 사항 저장
        try {
            LambEntity updatedLamb = lambRepository.save(lambEntity);
            log.info("양 수정 성공: ID={}, 품종명={}", updatedLamb.getLambId(), updatedLamb.getLambName());
            return updatedLamb.toDto();

        } catch (Exception e) {
            log.error("양 수정 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.LAMB_UPDATE_FAILED, e);
        }
    }

    /**
     * AL-03: 양 삭제 (관리자)
     * 관리자가 양 품종을 삭제
     *
     * 주의: 이미 사용자가 보유한 양이 있으면 삭제 불가 (외래키 제약)
     *
     * @param lambId 삭제할 양 ID
     * @throws CustomException 양을 찾을 수 없거나 삭제 실패 시
     */
    public void deleteLamb(int lambId) {
        // 1. 양 존재 여부 확인
        LambEntity lambEntity = lambRepository.findById(lambId)
                .orElseThrow(() -> {
                    log.warn("양 삭제 실패: 존재하지 않는 양 ID - {}", lambId);
                    return new CustomException(ErrorCode.LAMB_NOT_FOUND);
                });

        // 2. 해당 품종을 보유한 사용자가 있는지 확인
        long userCount = shepRepository.countByLambEntity_LambId(lambId);
        if (userCount > 0) {
            log.warn("양 삭제 실패: {}명의 사용자가 이 품종을 보유 중 - ID={}", userCount, lambId);
            throw new CustomException(
                    ErrorCode.LAMB_DELETE_FAILED,
                    "이 품종을 보유한 사용자가 있어 삭제할 수 없습니다."
            );
        }

        // 3. 이미지 파일 삭제
        if (lambEntity.getLambPath() != null) {
            try {
                fileUploadUtil.deleteFile(lambEntity.getLambPath());
                log.info("양 이미지 삭제 성공: {}", lambEntity.getLambPath());
            } catch (Exception e) {
                log.warn("양 이미지 삭제 실패 (파일이 없을 수 있음): {}", e.getMessage());
            }
        }

        // 4. 양 삭제
        try {
            lambRepository.delete(lambEntity);
            log.info("양 삭제 성공: ID={}, 품종명={}", lambId, lambEntity.getLambName());

        } catch (Exception e) {
            log.error("양 삭제 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.LAMB_DELETE_FAILED, e);
        }
    }

    // ========================================================
    // [관리자] 양 특성 관리 (AL-04, AL-05, AL-06)
    // ========================================================

    /**
     * AL-04: 양 특성 등록 (관리자)
     * 관리자가 새로운 양 특성을 등록
     *
     * @param request 양 특성 등록 요청 데이터
     * @return LambCharDto 등록된 양 특성 정보
     * @throws CustomException 유효성 검증 실패 또는 등록 실패 시
     */
    public LambCharDto createLambChar(LambCharCreateRequest request) {
        // 1. 요청 데이터 유효성 검증
        if (!request.isValid()) {
            log.warn("양 특성 등록 실패: 유효하지 않은 요청 데이터");
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    request.getValidationErrorMessage()
            );
        }

        // 2. 중복 특성명 확인
        if (lambCharRepository.existsByCharName(request.getCharName())) {
            log.warn("양 특성 등록 실패: 이미 존재하는 특성명 - {}", request.getCharName());
            throw new CustomException(
                    ErrorCode.LAMB_CHAR_CREATE_FAILED,
                    "이미 존재하는 특성명입니다."
            );
        }

        // 3. 양 특성 엔티티 생성
        LambCharEntity lambCharEntity = LambCharEntity.builder()
                .charName(request.getCharName())
                .charDesc(request.getCharDesc())
                .effectType(request.getEffectType())
                .effectValue(request.getEffectValue())
                .isActive(request.getIsActive())
                .build();

        // 4. 데이터베이스에 저장
        try {
            LambCharEntity savedChar = lambCharRepository.save(lambCharEntity);
            log.info("양 특성 등록 성공: ID={}, 특성명={}", savedChar.getCharId(), savedChar.getCharName());
            return savedChar.toDto();

        } catch (Exception e) {
            log.error("양 특성 등록 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.LAMB_CHAR_CREATE_FAILED, e);
        }
    }

    /**
     * AL-05: 양 특성 수정 (관리자)
     * 관리자가 기존 양 특성 정보를 수정
     *
     * @param charId 수정할 양 특성 ID
     * @param request 양 특성 수정 요청 데이터
     * @return LambCharDto 수정된 양 특성 정보
     * @throws CustomException 특성을 찾을 수 없거나 수정 실패 시
     */
    public LambCharDto updateLambChar(int charId, LambCharCreateRequest request) {
        // 1. 요청 데이터 유효성 검증
        if (!request.isValid()) {
            log.warn("양 특성 수정 실패: 유효하지 않은 요청 데이터");
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    request.getValidationErrorMessage()
            );
        }

        // 2. 기존 양 특성 조회
        LambCharEntity lambCharEntity = lambCharRepository.findById(charId)
                .orElseThrow(() -> {
                    log.warn("양 특성 수정 실패: 존재하지 않는 특성 ID - {}", charId);
                    return new CustomException(ErrorCode.LAMB_CHAR_NOT_FOUND);
                });

        // 3. 특성명 중복 확인
        lambCharRepository.findByCharName(request.getCharName())
                .ifPresent(existingChar -> {
                    if (existingChar.getCharId() != charId) {
                        throw new CustomException(
                                ErrorCode.LAMB_CHAR_UPDATE_FAILED,
                                "이미 존재하는 특성명입니다."
                        );
                    }
                });

        // 4. 수정 사항 반영
        lambCharEntity.setCharName(request.getCharName());
        lambCharEntity.setCharDesc(request.getCharDesc());
        lambCharEntity.setEffectType(request.getEffectType());
        lambCharEntity.setEffectValue(request.getEffectValue());
        lambCharEntity.setIsActive(request.getIsActive());

        // 5. 저장
        try {
            LambCharEntity updatedChar = lambCharRepository.save(lambCharEntity);
            log.info("양 특성 수정 성공: ID={}, 특성명={}", updatedChar.getCharId(), updatedChar.getCharName());
            return updatedChar.toDto();

        } catch (Exception e) {
            log.error("양 특성 수정 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.LAMB_CHAR_UPDATE_FAILED, e);
        }
    }

    /**
     * AL-06: 양 특성 삭제 (관리자)
     * 관리자가 양 특성을 삭제
     *
     * @param charId 삭제할 양 특성 ID
     * @throws CustomException 특성을 찾을 수 없거나 삭제 실패 시
     */
    public void deleteLambChar(int charId) {
        // 1. 양 특성 존재 여부 확인
        LambCharEntity lambCharEntity = lambCharRepository.findById(charId)
                .orElseThrow(() -> {
                    log.warn("양 특성 삭제 실패: 존재하지 않는 특성 ID - {}", charId);
                    return new CustomException(ErrorCode.LAMB_CHAR_NOT_FOUND);
                });

        // 2. 해당 특성을 사용하는 양이 있는지 확인
        long lambCount = lambRepository.countByCharId(charId);
        if (lambCount > 0) {
            log.warn("양 특성 삭제 실패: {}개의 양이 이 특성을 사용 중 - ID={}", lambCount, charId);
            throw new CustomException(
                    ErrorCode.LAMB_CHAR_DELETE_FAILED,
                    "이 특성을 사용하는 양이 있어 삭제할 수 없습니다."
            );
        }

        // 3. 삭제
        try {
            lambCharRepository.delete(lambCharEntity);
            log.info("양 특성 삭제 성공: ID={}, 특성명={}", charId, lambCharEntity.getCharName());

        } catch (Exception e) {
            log.error("양 특성 삭제 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.LAMB_CHAR_DELETE_FAILED, e);
        }
    }

    // ========================================================
    // [공통] 양/양 특성 조회 (LA-01, LA-02, LA-03, LA-04)
    // ========================================================

    /**
     * LA-01: 양 전체 조회
     * 권한에 따라 다르게 동작:
     * - 관리자: 모든 양 품종 조회
     * - 일반 사용자: 자신이 보유한 양만 조회
     *
     * @param userId 사용자 ID
     * @param isAdmin 관리자 여부
     * @param pageable 페이징 정보
     * @return PageResponse<LambDto> 페이징된 양 리스트
     */
    @Transactional(readOnly = true)  // 조회 전용 트랜잭션 (성능 최적화)
    public PageResponse<LambDto> getAllLambs(int userId, boolean isAdmin, Pageable pageable) {
        if (isAdmin) {
            // 관리자: 모든 양 품종 조회
            Page<LambEntity> lambPage = lambRepository.findAllWithChar(pageable);
            List<LambDto> lambDtos = lambPage.getContent().stream()
                    .map(LambEntity::toDto)
                    .collect(Collectors.toList());

            log.info("관리자 양 전체 조회: {}개", lambPage.getTotalElements());
            return PageResponse.of(lambDtos, lambPage);

        } else {
            // 일반 사용자: 자신이 보유한 양만 조회
            Page<ShepEntity> shepPage = shepRepository.findByUserIdWithLamb(userId, pageable);
            List<LambDto> lambDtos = shepPage.getContent().stream()
                    .map(shep -> shep.getLambEntity().toDto())
                    .collect(Collectors.toList());

            log.info("사용자 {} 양 조회: {}개", userId, shepPage.getTotalElements());
            return PageResponse.of(lambDtos, shepPage);
        }
    }

    /**
     * LA-02: 양 상세 조회
     * 권한에 따라 다르게 동작:
     * - 관리자: 모든 양 품종 상세 조회 가능
     * - 일반 사용자: 자신이 보유한 양만 상세 조회 가능
     *
     * @param lambId 양 ID
     * @param userId 사용자 ID
     * @param isAdmin 관리자 여부
     * @return LambDto 양 상세 정보
     * @throws CustomException 양을 찾을 수 없거나 권한이 없을 때
     */
    @Transactional(readOnly = true)
    public LambDto getDetailLamb(int lambId, int userId, boolean isAdmin) {
        // 양 조회
        LambEntity lambEntity = lambRepository.findById(lambId)
                .orElseThrow(() -> {
                    log.warn("양 상세 조회 실패: 존재하지 않는 양 ID - {}", lambId);
                    return new CustomException(ErrorCode.LAMB_NOT_FOUND);
                });

        // 일반 사용자인 경우 소유권 확인
        if (!isAdmin) {
            boolean owns = shepRepository.existsByUsersEntity_UserIdAndLambEntity_LambId(userId, lambId);
            if (!owns) {
                log.warn("양 상세 조회 실패: 사용자 {}가 양 {}를 보유하고 있지 않음", userId, lambId);
                throw new CustomException(ErrorCode.FORBIDDEN, "자신이 보유한 양만 조회할 수 있습니다.");
            }
        }

        log.info("양 상세 조회 성공: ID={}", lambId);
        return lambEntity.toDto();
    }

    /**
     * LA-03: 양 특성 전체 조회
     *
     * @param pageable 페이징 정보
     * @return PageResponse<LambCharDto> 페이징된 양 특성 리스트
     */
    @Transactional(readOnly = true)
    public PageResponse<LambCharDto> getAllLambChars(Pageable pageable) {
        // 활성화된 특성만 조회
        Page<LambCharEntity> charPage = lambCharRepository.findByIsActive(1, pageable);
        List<LambCharDto> charDtos = charPage.getContent().stream()
                .map(LambCharEntity::toDto)
                .collect(Collectors.toList());

        log.info("양 특성 전체 조회: {}개", charPage.getTotalElements());
        return PageResponse.of(charDtos, charPage);
    }

    /**
     * LA-04: 양 특성 상세 조회
     *
     * @param charId 양 특성 ID
     * @return LambCharDto 양 특성 상세 정보
     * @throws CustomException 특성을 찾을 수 없을 때
     */
    @Transactional(readOnly = true)
    public LambCharDto getDetailLambChar(int charId) {
        LambCharEntity lambCharEntity = lambCharRepository.findById(charId)
                .orElseThrow(() -> {
                    log.warn("양 특성 상세 조회 실패: 존재하지 않는 특성 ID - {}", charId);
                    return new CustomException(ErrorCode.LAMB_CHAR_NOT_FOUND);
                });

        log.info("양 특성 상세 조회 성공: ID={}", charId);
        return lambCharEntity.toDto();
    }

    // ========================================================
    // [사용자] 양 관리 (LA-05, LA-06, LA-07, LA-08)
    // ========================================================

    /**
     * LA-05: 양 이름 짓기
     * 사용자가 자신의 양에게 별명을 지어줌
     *
     * @param shepId 양치기 ID
     * @param userId 사용자 ID
     * @param request 이름 짓기 요청 데이터
     * @return ShepDto 수정된 양치기 정보
     * @throws CustomException 양을 찾을 수 없거나 소유권이 없을 때
     */
    public ShepDto namingLamb(int shepId, int userId, ShepActionRequest request) {
        // 1. 요청 데이터 유효성 검증
        if (!request.isValidForNaming()) {
            log.warn("양 이름 짓기 실패: 유효하지 않은 요청 데이터");
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    request.getNamingValidationError()
            );
        }

        // 2. 양치기 조회 및 소유권 확인
        ShepEntity shepEntity = shepRepository.findByShepIdAndUsersEntity_UserId(shepId, userId)
                .orElseThrow(() -> {
                    log.warn("양 이름 짓기 실패: 존재하지 않거나 소유하지 않은 양 - shepId={}, userId={}", shepId, userId);
                    return new CustomException(ErrorCode.SHEP_NOT_OWNED);
                });

        // 3. 늑대에게 쫓기는 중인지 확인
        if (shepEntity.getShepExist() == -1) {
            log.warn("양 이름 짓기 실패: 늑대에게 쫓기는 양 - shepId={}", shepId);
            throw new CustomException(ErrorCode.SHEP_CHASED_BY_WOLF);
        }

        // 4. 이름 변경
        shepEntity.setShepName(request.getShepName());

        // 5. 저장
        try {
            ShepEntity updatedShep = shepRepository.save(shepEntity);
            log.info("양 이름 짓기 성공: shepId={}, 새 이름={}", shepId, request.getShepName());
            return updatedShep.toDto();

        } catch (Exception e) {
            log.error("양 이름 짓기 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.SHEP_NAMING_FAILED, e);
        }
    }

    /**
     * LA-06: 양 밥 주기
     * 사용자가 자신의 양에게 밥을 줌
     *
     * shepHunger 변화:
     * - -1 (배고픔) → 0 (보통)
     * - 0 (보통) → 1 (배부름)
     * - 1 (배부름) → 변화 없음 (이미 배부름)
     *
     * @param shepId 양치기 ID
     * @param userId 사용자 ID
     * @return ShepDto 수정된 양치기 정보
     * @throws CustomException 양을 찾을 수 없거나 이미 배부를 때
     */
    public ShepDto feedingLamb(int shepId, int userId) {
        // 1. 양치기 조회 및 소유권 확인
        ShepEntity shepEntity = shepRepository.findByShepIdAndUsersEntity_UserId(shepId, userId)
                .orElseThrow(() -> {
                    log.warn("양 밥 주기 실패: 존재하지 않거나 소유하지 않은 양 - shepId={}, userId={}", shepId, userId);
                    return new CustomException(ErrorCode.SHEP_NOT_OWNED);
                });

        // 2. 늑대에게 쫓기는 중인지 확인
        if (shepEntity.getShepExist() == -1) {
            log.warn("양 밥 주기 실패: 늑대에게 쫓기는 양 - shepId={}", shepId);
            throw new CustomException(ErrorCode.SHEP_CHASED_BY_WOLF);
        }

        // 3. 현재 배고픔 상태 확인
        int currentHunger = shepEntity.getShepHunger();

        if (currentHunger == 1) {
            // 이미 배부름
            log.warn("양 밥 주기 실패: 이미 배부른 양 - shepId={}", shepId);
            throw new CustomException(ErrorCode.SHEP_ALREADY_FED);
        }

        // 4. 배고픔 상태 변경 (-1 → 0, 0 → 1)
        shepEntity.setShepHunger(currentHunger + 1);

        // 5. 저장
        try {
            ShepEntity updatedShep = shepRepository.save(shepEntity);
            log.info("양 밥 주기 성공: shepId={}, 배고픔 {} → {}", shepId, currentHunger, updatedShep.getShepHunger());
            return updatedShep.toDto();

        } catch (Exception e) {
            log.error("양 밥 주기 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.SHEP_FEEDING_FAILED, e);
        }
    }

    /**
     * LA-07: 양 털 깎기
     * 사용자가 자신의 양의 털을 깎음
     *
     * shepFur 변화:
     * - -1 (털 많음) → 1 (털 없음)
     * - 0 (털 보통) → 1 (털 없음)
     * - 1 (털 없음) → 변화 없음 (이미 털 없음)
     *
     * @param shepId 양치기 ID
     * @param userId 사용자 ID
     * @return ShepDto 수정된 양치기 정보
     * @throws CustomException 양을 찾을 수 없거나 이미 털이 없을 때
     */
    public ShepDto shavingLamb(int shepId, int userId) {
        // 1. 양치기 조회 및 소유권 확인
        ShepEntity shepEntity = shepRepository.findByShepIdAndUsersEntity_UserId(shepId, userId)
                .orElseThrow(() -> {
                    log.warn("양 털 깎기 실패: 존재하지 않거나 소유하지 않은 양 - shepId={}, userId={}", shepId, userId);
                    return new CustomException(ErrorCode.SHEP_NOT_OWNED);
                });

        // 2. 늑대에게 쫓기는 중인지 확인
        if (shepEntity.getShepExist() == -1) {
            log.warn("양 털 깎기 실패: 늑대에게 쫓기는 양 - shepId={}", shepId);
            throw new CustomException(ErrorCode.SHEP_CHASED_BY_WOLF);
        }

        // 3. 현재 털 상태 확인
        int currentFur = shepEntity.getShepFur();

        if (currentFur == 1) {
            // 이미 털 없음
            log.warn("양 털 깎기 실패: 이미 털이 없는 양 - shepId={}", shepId);
            throw new CustomException(ErrorCode.SHEP_NO_FUR);
        }

        // 4. 털 상태 변경 (→ 1: 털 없음)
        shepEntity.setShepFur(1);

        // 5. 저장
        try {
            ShepEntity updatedShep = shepRepository.save(shepEntity);
            log.info("양 털 깎기 성공: shepId={}, 털 상태 {} → {}", shepId, currentFur, updatedShep.getShepFur());
            return updatedShep.toDto();

        } catch (Exception e) {
            log.error("양 털 깎기 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.SHEP_SHAVING_FAILED, e);
        }
    }

    /**
     * LA-08: 양 장소 옮기기
     * 양을 울타리 ↔ 목장 사이에서 이동
     *
     * shepExist 변화:
     * - 0 (목장) → 1 (울타리)
     * - 1 (울타리) → 0 (목장)
     *
     * @param shepId 양치기 ID
     * @param userId 사용자 ID
     * @param request 장소 이동 요청 데이터
     * @return ShepDto 수정된 양치기 정보
     * @throws CustomException 양을 찾을 수 없거나 소유권이 없을 때
     */
    public ShepDto movingLamb(int shepId, int userId, ShepActionRequest request) {
        // 1. 요청 데이터 유효성 검증
        if (!request.isValidForMoving()) {
            log.warn("양 장소 옮기기 실패: 유효하지 않은 요청 데이터");
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    request.getMovingValidationError()
            );
        }

        // 2. 양치기 조회 및 소유권 확인
        ShepEntity shepEntity = shepRepository.findByShepIdAndUsersEntity_UserId(shepId, userId)
                .orElseThrow(() -> {
                    log.warn("양 장소 옮기기 실패: 존재하지 않거나 소유하지 않은 양 - shepId={}, userId={}", shepId, userId);
                    return new CustomException(ErrorCode.SHEP_NOT_OWNED);
                });

        // 3. 늑대에게 쫓기는 중인지 확인
        if (shepEntity.getShepExist() == -1) {
            log.warn("양 장소 옮기기 실패: 늑대에게 쫓기는 양 - shepId={}", shepId);
            throw new CustomException(ErrorCode.SHEP_CHASED_BY_WOLF);
        }

        // 4. 장소 변경
        int oldLocation = shepEntity.getShepExist();
        shepEntity.setShepExist(request.getShepExist());

        // 5. 저장
        try {
            ShepEntity updatedShep = shepRepository.save(shepEntity);
            String fromLocation = oldLocation == 0 ? "목장" : "울타리";
            String toLocation = updatedShep.getShepExist() == 0 ? "목장" : "울타리";
            log.info("양 장소 옮기기 성공: shepId={}, {} → {}", shepId, fromLocation, toLocation);
            return updatedShep.toDto();

        } catch (Exception e) {
            log.error("양 장소 옮기기 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.SHEP_MOVING_FAILED, e);
        }
    }

    // ========================================================
    // [사용자] 양 등장/실종 (LA-09, LA-10)
    // ========================================================

    /**
     * LA-09: 양 등장
     * 약속 평가에 따라 양 또는 늑대가 등장
     *
     * 동작 흐름:
     * 1. 약속 공유 ID로 확률 정보 조회
     * 2. 양/늑대 등장 확률 계산
     * 3. 양이 등장하면 등급 결정
     * 4. 새로운 ShepEntity 생성
     *
     * @param userId 사용자 ID
     * @param shareId 약속 공유 ID
     * @return ShepDto 등장한 양 정보 (늑대가 등장하면 null)
     * @throws CustomException 확률 정보를 찾을 수 없을 때
     */
    public ShepDto appearLamb(int userId, int shareId) {
        // 1. 사용자 조회
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        // 2. 약속 공유의 확률 정보 조회
        ProbEntity probEntity = probRepository.findByShareEntity_ShareId(shareId)
                .orElseThrow(() -> {
                    log.warn("양 등장 실패: 확률 정보 없음 - shareId={}", shareId);
                    return new CustomException(ErrorCode.PROB_NOT_FOUND);
                });

        // 3. 양 vs 늑대 등장 결정
        int randomValue = random.nextInt(100);  // 0~99
        boolean isLambAppear = randomValue < probEntity.getProbLamb();

        if (!isLambAppear) {
            // 늑대 등장
            log.info("늑대 등장: userId={}, shareId={}, 확률={}", userId, shareId, probEntity.getProbWolf());
            return null;  // 늑대가 등장하면 null 반환
        }

        // 4. 양 등장 - 등급 결정
        LambRank selectedRank = selectLambRank(probEntity.getProbRare());

        // 5. 해당 등급의 양 중 랜덤 선택
        List<LambEntity> lambsOfRank = lambRepository.findByLambRank(selectedRank);
        if (lambsOfRank.isEmpty()) {
            log.warn("양 등장 실패: 등급 {}에 해당하는 양이 없음", selectedRank);
            throw new CustomException(ErrorCode.SHEP_APPEAR_FAILED, "등장할 수 있는 양이 없습니다.");
        }

        LambEntity selectedLamb = lambsOfRank.get(random.nextInt(lambsOfRank.size()));

        // 6. 새로운 ShepEntity 생성
        ShepEntity newShep = ShepEntity.builder()
                .shepName(selectedLamb.getLambName())  // 초기 이름은 품종명
                .shepHunger(1)                          // 배부름
                .shepFur(1)                             // 털 없음
                .shepExist(0)                           // 목장에 등장
                .lambEntity(selectedLamb)
                .usersEntity(user)
                .build();

        // 7. 저장
        try {
            ShepEntity savedShep = shepRepository.save(newShep);
            log.info("양 등장 성공: userId={}, lambId={}, rank={}", userId, selectedLamb.getLambId(), selectedRank);
            return savedShep.toDto();

        } catch (Exception e) {
            log.error("양 등장 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.SHEP_APPEAR_FAILED, e);
        }
    }

    /**
     * 양 등급 결정 로직
     * probRare 보정값을 적용하여 희귀 등급 확률 조정
     *
     * 기본 확률:
     * - 일반(COMMON): 66%
     * - 희귀(RARE): 25%
     * - 특급(SPECIAL): 8%
     * - 전설(LEGENDARY): 1%
     *
     * @param probRare 희귀 등급 확률 보정값 (-100 ~ 100)
     * @return LambRank 결정된 등급
     */
    private LambRank selectLambRank(int probRare) {
        // 1. 기본 확률 가져오기
        int commonProb = LambRank.COMMON.prob;
        int rareProb = LambRank.RARE.prob;
        int specialProb = LambRank.SPECIAL.prob;
        int legendaryProb = LambRank.LEGENDARY.prob;

        // 2. probRare 보정 적용
        // 희귀 등급들의 확률 증가, 일반 등급 확률 감소
        if (probRare > 0) {
            // 양수: 희귀 등급 확률 증가
            int bonus = probRare;
            commonProb = Math.max(1, commonProb - bonus);  // 최소 1% 보장

            // 보너스를 희귀~전설 등급에 분배
            rareProb += (int)(bonus * 0.6);
            specialProb += (int)(bonus * 0.3);
            legendaryProb += (int)(bonus * 0.1);

        } else if (probRare < 0) {
            // 음수: 희귀 등급 확률 감소
            int penalty = Math.abs(probRare);

            rareProb = Math.max(0, rareProb - (int)(penalty * 0.6));
            specialProb = Math.max(0, specialProb - (int)(penalty * 0.3));
            legendaryProb = Math.max(0, legendaryProb - (int)(penalty * 0.1));

            commonProb = 100 - rareProb - specialProb - legendaryProb;
        }

        // 3. 누적 확률 계산
        int[] cumulativeProb = new int[4];
        cumulativeProb[0] = commonProb;                                    // 일반
        cumulativeProb[1] = cumulativeProb[0] + rareProb;                  // 희귀
        cumulativeProb[2] = cumulativeProb[1] + specialProb;               // 특급
        cumulativeProb[3] = cumulativeProb[2] + legendaryProb;             // 전설

        // 4. 랜덤 값으로 등급 결정
        int randomValue = random.nextInt(100);

        if (randomValue < cumulativeProb[0]) {
            return LambRank.COMMON;
        } else if (randomValue < cumulativeProb[1]) {
            return LambRank.RARE;
        } else if (randomValue < cumulativeProb[2]) {
            return LambRank.SPECIAL;
        } else {
            return LambRank.LEGENDARY;
        }
    }

    /**
     * LA-10: 양 실종
     * 늑대에게 양이 쫓겨서 사라짐
     *
     * 처리 방법:
     * - shepExist를 -1로 설정 (늑대에게 쫓기는 중)
     * - 또는 완전히 삭제
     *
     * @param shepId 양치기 ID
     * @param userId 사용자 ID
     * @throws CustomException 양을 찾을 수 없거나 소유권이 없을 때
     */
    public void missingLamb(int shepId, int userId) {
        // 1. 양치기 조회 및 소유권 확인
        ShepEntity shepEntity = shepRepository.findByShepIdAndUsersEntity_UserId(shepId, userId)
                .orElseThrow(() -> {
                    log.warn("양 실종 실패: 존재하지 않거나 소유하지 않은 양 - shepId={}, userId={}", shepId, userId);
                    return new CustomException(ErrorCode.SHEP_NOT_OWNED);
                });

        // 2. 실종 처리 (shepExist를 -1로 설정)
        shepEntity.setShepExist(-1);

        // 3. 저장
        try {
            shepRepository.save(shepEntity);
            log.info("양 실종 처리 성공: shepId={}, userId={}", shepId, userId);

        } catch (Exception e) {
            log.error("양 실종 처리 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.SHEP_MISSING_FAILED, e);
        }

        // 또는 완전히 삭제하려면:
        // shepRepository.delete(shepEntity);
    }

    // ========================================================
    // [확률 관리] (LA-11, LA-12, LA-13)
    // ========================================================

    /**
     * LA-11, LA-12, LA-13: 등장 확률 변경
     * 약속 공유에 대한 양/늑대/희귀등급 등장 확률 생성 또는 수정
     *
     * @param shareId 약속 공유 ID
     * @param request 확률 변경 요청 데이터
     * @return ProbDto 변경된 확률 정보
     * @throws CustomException 약속 공유를 찾을 수 없거나 확률 변경 실패 시
     */
    public ProbDto updateProb(int shareId, ProbUpdateRequest request) {
        // 1. 요청 데이터 유효성 검증
        if (!request.isValid() || !request.hasUpdateData()) {
            log.warn("확률 변경 실패: 유효하지 않은 요청 데이터");
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    request.getValidationErrorMessage()
            );
        }

        // 2. 약속 공유 조회
        ShareEntity shareEntity = shareRepository.findById(shareId)
                .orElseThrow(() -> {
                    log.warn("확률 변경 실패: 존재하지 않는 약속 공유 - shareId={}", shareId);
                    return new CustomException(ErrorCode.PROB_NOT_FOUND, "약속 공유를 찾을 수 없습니다.");
                });

        // 3. 기존 확률 정보 조회 또는 새로 생성
        ProbEntity probEntity = probRepository.findByShareEntity_ShareId(shareId)
                .orElseGet(() -> {
                    // 확률 정보가 없으면 새로 생성
                    log.info("새로운 확률 정보 생성: shareId={}", shareId);
                    return ProbEntity.builder()
                            .probLamb(70)        // 기본값: 양 70%
                            .probWolf(30)        // 기본값: 늑대 30%
                            .probRare(0)         // 기본값: 보정 없음
                            .shareEntity(shareEntity)
                            .build();
                });

        // 4. 확률 업데이트
        if (request.getProbLamb() != null) {
            probEntity.setProbLamb(request.getProbLamb());
            // 양 확률이 변경되면 늑대 확률도 자동 조정
            probEntity.setProbWolf(100 - request.getProbLamb());
        }

        if (request.getProbWolf() != null) {
            probEntity.setProbWolf(request.getProbWolf());
            // 늑대 확률이 변경되면 양 확률도 자동 조정
            probEntity.setProbLamb(100 - request.getProbWolf());
        }

        if (request.getProbRare() != null) {
            probEntity.setProbRare(request.getProbRare());
        }

        // 5. 저장
        try {
            ProbEntity savedProb = probRepository.save(probEntity);
            log.info("확률 변경 성공: shareId={}, lamb={}%, wolf={}%, rare={}",
                    shareId, savedProb.getProbLamb(), savedProb.getProbWolf(), savedProb.getProbRare());
            return savedProb.toDto();

        } catch (Exception e) {
            log.error("확률 변경 중 데이터베이스 오류 발생", e);
            throw new CustomException(ErrorCode.PROB_UPDATE_FAILED, e);
        }
    }

    /**
     * 특정 약속 공유의 확률 정보 조회
     *
     * @param shareId 약속 공유 ID
     * @return ProbDto 확률 정보
     * @throws CustomException 확률 정보를 찾을 수 없을 때
     */
    @Transactional(readOnly = true)
    public ProbDto getProb(int shareId) {
        ProbEntity probEntity = probRepository.findByShareEntity_ShareId(shareId)
                .orElseThrow(() -> {
                    log.warn("확률 조회 실패: 확률 정보 없음 - shareId={}", shareId);
                    return new CustomException(ErrorCode.PROB_NOT_FOUND);
                });

        log.info("확률 조회 성공: shareId={}", shareId);
        return probEntity.toDto();
    }
}