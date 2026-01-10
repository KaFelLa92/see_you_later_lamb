package web.repository.point;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.model.entity.point.PayEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 포인트 지급(Point Pay) Repository
 * 포인트 지급 내역 데이터베이스 접근을 담당하는 인터페이스
 */
@Repository
public interface PayRepository extends JpaRepository<PayEntity, Integer> {

    // ========== 기본 제공 메서드 (JpaRepository가 자동 제공) ==========
    // save(PayEntity) - 포인트 지급 내역 저장
    // findById(Integer) - ID로 포인트 지급 내역 조회
    // findAll() - 전체 포인트 지급 내역 조회
    // delete(PayEntity) - 포인트 지급 내역 삭제

    // ========== 출석 관련 포인트 조회 ==========

    /**
     * 특정 출석에 대한 포인트 지급 내역 조회
     *
     * @param atenId 출석 ID
     * @return Optional<PayEntity> 포인트 지급 내역
     */
    Optional<PayEntity> findByAtenEntity_AtenId(int atenId);

    /**
     * 특정 출석에 대한 포인트 지급 여부 확인
     *
     * @param atenId 출석 ID
     * @return boolean 지급되었으면 true
     */
    boolean existsByAtenEntity_AtenId(int atenId);

    // ========== 약속 공유 관련 포인트 조회 ==========

    /**
     * 특정 약속 공유에 대한 포인트 지급 내역 조회
     *
     * @param shareId 약속 공유 ID
     * @return Optional<PayEntity> 포인트 지급 내역
     */
    Optional<PayEntity> findByShareEntity_ShareId(int shareId);

    /**
     * 특정 약속 공유에 대한 포인트 지급 여부 확인
     *
     * @param shareId 약속 공유 ID
     * @return boolean 지급되었으면 true
     */
    boolean existsByShareEntity_ShareId(int shareId);

    // ========== 목장 업무 관련 포인트 조회 ==========

    /**
     * 특정 목장 업무에 대한 포인트 지급 내역 조회
     *
     * @param workId 목장 업무 ID
     * @return Optional<PayEntity> 포인트 지급 내역
     */
    Optional<PayEntity> findByWorkEntity_WorkId(int workId);

    /**
     * 특정 목장 업무에 대한 포인트 지급 여부 확인
     *
     * @param workId 목장 업무 ID
     * @return boolean 지급되었으면 true
     */
    boolean existsByWorkEntity_WorkId(int workId);

    // ========== 목장 구매 관련 포인트 조회 ==========

    /**
     * 특정 목장 구매에 대한 포인트 차감 내역 조회
     *
     * @param farmId 목장 ID
     * @return List<PayEntity> 포인트 차감 내역 리스트
     */
    List<PayEntity> findByFarmEntity_FarmId(int farmId);

    // ========== 포인트 정책별 조회 ==========

    /**
     * 특정 포인트 정책으로 지급된 내역 조회
     *
     * @param pointId 포인트 정책 ID
     * @return List<PayEntity> 포인트 지급 내역 리스트
     */
    List<PayEntity> findByPointEntity_PointId(int pointId);

    /**
     * 특정 포인트 정책으로 지급된 내역 조회 (페이징)
     *
     * @param pointId 포인트 정책 ID
     * @param pageable 페이징 정보
     * @return Page<PayEntity> 페이징된 포인트 지급 내역 리스트
     */
    Page<PayEntity> findByPointEntity_PointId(int pointId, Pageable pageable);

    // ========== 기간별 조회 ==========

    /**
     * 특정 기간 내 포인트 지급 내역 조회
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return List<PayEntity> 포인트 지급 내역 리스트
     */
    @Query("SELECT p FROM PayEntity p WHERE p.createDate BETWEEN :startDate AND :endDate")
    List<PayEntity> findByDateBetween(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    // ========== 통계 조회 ==========

    /**
     * 특정 포인트 정책의 총 지급 횟수
     *
     * @param pointId 포인트 정책 ID
     * @return long 지급 횟수
     */
    long countByPointEntity_PointId(int pointId);

    /**
     * 특정 기간 내 총 포인트 지급 횟수
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return long 지급 횟수
     */
    @Query("SELECT COUNT(p) FROM PayEntity p WHERE p.createDate BETWEEN :startDate AND :endDate")
    long countByDateBetween(@Param("startDate") LocalDateTime startDate,
                            @Param("endDate") LocalDateTime endDate);

    // ========== 복합 조회 (모든 연관 엔티티 포함) ==========

    /**
     * 포인트 지급 내역을 모든 연관 엔티티와 함께 조회
     *
     * JOIN FETCH로 N+1 문제 해결
     *
     * @param payId 포인트 지급 ID
     * @return Optional<PayEntity> 포인트 지급 내역 (모든 연관 엔티티 포함)
     */
    @Query("SELECT p FROM PayEntity p " +
            "LEFT JOIN FETCH p.atenEntity " +
            "LEFT JOIN FETCH p.shareEntity " +
            "LEFT JOIN FETCH p.workEntity " +
            "LEFT JOIN FETCH p.farmEntity " +
            "LEFT JOIN FETCH p.pointEntity " +
            "WHERE p.payId = :payId")
    Optional<PayEntity> findByIdWithAll(@Param("payId") int payId);

    //  AP-05	포인트 지급/삭감	create_point()
    //  → save() 메서드 사용
}