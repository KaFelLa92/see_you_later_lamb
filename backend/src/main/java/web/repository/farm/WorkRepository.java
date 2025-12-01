package web.repository.farm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.model.entity.farm.WorkEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 목장 업무(Farm Work) Repository
 * 목장 업무 데이터베이스 접근을 담당하는 인터페이스
 */
@Repository
public interface WorkRepository extends JpaRepository<WorkEntity, Integer> {

    // ========== 기본 제공 메서드 (JpaRepository가 자동 제공) ==========
    // save(WorkEntity) - 업무 저장/수정
    // findById(Integer) - ID로 업무 조회
    // findAll() - 전체 업무 조회
    // delete(WorkEntity) - 업무 삭제

    // ========== 목장주별 업무 조회 ==========

    /**
     * 특정 목장주의 모든 업무 조회
     *
     * @param ownerId 목장주 ID
     * @return List<WorkEntity> 업무 리스트
     */
    List<WorkEntity> findByOwnerEntity_OwnerId(int ownerId);

    /**
     * 특정 목장주의 모든 업무 조회 (페이징)
     *
     * @param ownerId 목장주 ID
     * @param pageable 페이징 정보
     * @return Page<WorkEntity> 페이징된 업무 리스트
     */
    Page<WorkEntity> findByOwnerEntity_OwnerId(int ownerId, Pageable pageable);

    /**
     * 특정 목장주의 업무 개수 조회
     *
     * @param ownerId 목장주 ID
     * @return long 업무 개수
     */
    long countByOwnerEntity_OwnerId(int ownerId);

    // ========== 업무 상태별 조회 ==========

    /**
     * 특정 목장주의 특정 상태 업무 조회
     *
     * workState:
     * - -1: 기한 종료 (실패)
     * - 0: 미실행 (대기 중)
     * - 1: 완료 (성공)
     *
     * @param ownerId 목장주 ID
     * @param workState 업무 상태
     * @return List<WorkEntity> 업무 리스트
     */
    List<WorkEntity> findByOwnerEntity_OwnerIdAndWorkState(int ownerId, int workState);

    /**
     * 특정 목장주의 미완료 업무 조회 (대기 중)
     *
     * @param ownerId 목장주 ID
     * @return List<WorkEntity> 업무 리스트
     */
    @Query("SELECT w FROM WorkEntity w WHERE w.ownerEntity.ownerId = :ownerId AND w.workState = 0")
    List<WorkEntity> findPendingWorks(@Param("ownerId") int ownerId);

    /**
     * 특정 목장주의 완료된 업무 조회
     *
     * @param ownerId 목장주 ID
     * @return List<WorkEntity> 업무 리스트
     */
    @Query("SELECT w FROM WorkEntity w WHERE w.ownerEntity.ownerId = :ownerId AND w.workState = 1")
    List<WorkEntity> findCompletedWorks(@Param("ownerId") int ownerId);

    /**
     * 특정 목장주의 실패한 업무 조회
     *
     * @param ownerId 목장주 ID
     * @return List<WorkEntity> 업무 리스트
     */
    @Query("SELECT w FROM WorkEntity w WHERE w.ownerEntity.ownerId = :ownerId AND w.workState = -1")
    List<WorkEntity> findFailedWorks(@Param("ownerId") int ownerId);

    // ========== 기한 관련 조회 ==========

    /**
     * 기한이 지난 업무 조회 (전체)
     *
     * @param now 현재 시간
     * @return List<WorkEntity> 업무 리스트
     */
    @Query("SELECT w FROM WorkEntity w WHERE w.workEndDate < :now AND w.workState = 0")
    List<WorkEntity> findExpiredWorks(@Param("now") LocalDateTime now);

    /**
     * 특정 목장주의 기한이 지난 업무 조회
     *
     * @param ownerId 목장주 ID
     * @param now 현재 시간
     * @return List<WorkEntity> 업무 리스트
     */
    @Query("SELECT w FROM WorkEntity w " +
            "WHERE w.ownerEntity.ownerId = :ownerId " +
            "AND w.workEndDate < :now " +
            "AND w.workState = 0")
    List<WorkEntity> findExpiredWorksByOwnerId(@Param("ownerId") int ownerId,
                                               @Param("now") LocalDateTime now);

    /**
     * 특정 기간 내에 종료되는 업무 조회
     *
     * @param ownerId 목장주 ID
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return List<WorkEntity> 업무 리스트
     */
    @Query("SELECT w FROM WorkEntity w " +
            "WHERE w.ownerEntity.ownerId = :ownerId " +
            "AND w.workEndDate BETWEEN :startDate AND :endDate")
    List<WorkEntity> findWorksByEndDateBetween(@Param("ownerId") int ownerId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    // ========== 복합 조회 (목장주 정보와 함께) ==========

    /**
     * 특정 업무를 목장주 정보와 함께 조회
     *
     * JOIN FETCH로 N+1 문제 해결
     *
     * @param workId 업무 ID
     * @return Optional<WorkEntity> 업무 엔티티 (목장주 정보 포함)
     */
    @Query("SELECT w FROM WorkEntity w " +
            "JOIN FETCH w.ownerEntity " +
            "WHERE w.workId = :workId")
    Optional<WorkEntity> findByIdWithOwner(@Param("workId") int workId);

    /**
     * 특정 사용자의 모든 업무 조회 (목장주를 통해)
     *
     * @param userId 사용자 ID
     * @return List<WorkEntity> 업무 리스트
     */
    @Query("SELECT w FROM WorkEntity w " +
            "JOIN w.ownerEntity o " +
            "WHERE o.usersEntity.userId = :userId")
    List<WorkEntity> findWorksByUserId(@Param("userId") int userId);

    /**
     * 특정 업무 ID와 사용자 ID로 조회 (소유권 확인용)
     *
     * @param workId 업무 ID
     * @param userId 사용자 ID
     * @return Optional<WorkEntity> 업무 엔티티
     */
    @Query("SELECT w FROM WorkEntity w " +
            "JOIN w.ownerEntity o " +
            "WHERE w.workId = :workId AND o.usersEntity.userId = :userId")
    Optional<WorkEntity> findByWorkIdAndUserId(@Param("workId") int workId,
                                               @Param("userId") int userId);

    //  FA-03	업무 전체조회(+관)	get_work()
    //  → findAll() 또는 findByOwnerEntity_OwnerId() 사용

    //  FA-04	업무 상세조회(+관)	get_detail_work()
    //  → findById() 또는 findByWorkIdAndUserId() 사용

    //  FA-07	목장 업무 처리	working_farm()
    //  1) 기한이 지나기 전: save() 메서드로 workState와 workEndDate 갱신
    //  2) 기한 지난 후: findExpiredWorks()로 조회 후 workState를 -1로 갱신
}