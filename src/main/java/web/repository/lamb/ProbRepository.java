package web.repository.lamb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.model.entity.lamb.ProbEntity;

import java.util.List;
import java.util.Optional;

/**
 * 등장 확률(Probability) Repository
 * 양/늑대 등장 확률 데이터베이스 접근을 담당하는 인터페이스
 */
@Repository
public interface ProbRepository extends JpaRepository<ProbEntity, Integer> {

    // ========== 기본 제공 메서드 (JpaRepository가 자동 제공) ==========
    // save(ProbEntity) - 확률 저장/수정
    // findById(Integer) - ID로 확률 조회
    // findAll() - 전체 확률 조회
    // delete(ProbEntity) - 확률 삭제

    // ========== 약속 공유별 확률 조회 ==========

    /**
     * 특정 약속 공유의 확률 정보 조회
     *
     * 연관 관계 탐색:
     * - shareEntity.shareId로 접근
     *
     * 생성되는 쿼리:
     * SELECT * FROM probability WHERE share_id = ?
     *
     * @param shareId 약속 공유 ID
     * @return Optional<ProbEntity> 확률 엔티티
     */
    Optional<ProbEntity> findByShareEntity_ShareId(int shareId);

    /**
     * 특정 약속 공유의 확률 정보 존재 여부 확인
     *
     * @param shareId 약속 공유 ID
     * @return boolean 존재하면 true
     */
    boolean existsByShareEntity_ShareId(int shareId);

    /**
     * 약속 공유 ID 리스트로 확률 정보 조회
     *
     * @param shareIds 약속 공유 ID 리스트
     * @return List<ProbEntity> 확률 리스트
     */
    @Query("SELECT p FROM ProbEntity p WHERE p.shareEntity.shareId IN :shareIds")
    List<ProbEntity> findByShareIds(@Param("shareIds") List<Integer> shareIds);

    // ========== 확률 범위 조회 ==========

    /**
     * 양 등장 확률이 특정 값 이상인 확률 정보 조회
     *
     * @param minProb 최소 확률
     * @return List<ProbEntity> 확률 리스트
     */
    @Query("SELECT p FROM ProbEntity p WHERE p.probLamb >= :minProb")
    List<ProbEntity> findByLambProbGreaterThanEqual(@Param("minProb") int minProb);

    /**
     * 늑대 등장 확률이 특정 값 이상인 확률 정보 조회
     *
     * @param minProb 최소 확률
     * @return List<ProbEntity> 확률 리스트
     */
    @Query("SELECT p FROM ProbEntity p WHERE p.probWolf >= :minProb")
    List<ProbEntity> findByWolfProbGreaterThanEqual(@Param("minProb") int minProb);

    /**
     * 희귀 등급 확률이 특정 값 이상인 확률 정보 조회
     *
     * @param minProb 최소 확률
     * @return List<ProbEntity> 확률 리스트
     */
    @Query("SELECT p FROM ProbEntity p WHERE p.probRare >= :minProb")
    List<ProbEntity> findByRareProbGreaterThanEqual(@Param("minProb") int minProb);

    // ========== 통계 조회 ==========

    /**
     * 양 등장 확률의 평균값 조회
     *
     * @return Double 평균 확률
     */
    @Query("SELECT AVG(p.probLamb) FROM ProbEntity p")
    Double getAverageLambProb();

    /**
     * 늑대 등장 확률의 평균값 조회
     *
     * @return Double 평균 확률
     */
    @Query("SELECT AVG(p.probWolf) FROM ProbEntity p")
    Double getAverageWolfProb();

    /**
     * 희귀 등급 확률의 평균값 조회
     *
     * @return Double 평균 확률
     */
    @Query("SELECT AVG(p.probRare) FROM ProbEntity p")
    Double getAverageRareProb();

    /**
     * 최대 양 등장 확률 조회
     *
     * @return Integer 최대 확률
     */
    @Query("SELECT MAX(p.probLamb) FROM ProbEntity p")
    Integer getMaxLambProb();

    /**
     * 최소 양 등장 확률 조회
     *
     * @return Integer 최소 확률
     */
    @Query("SELECT MIN(p.probLamb) FROM ProbEntity p")
    Integer getMinLambProb();

    // ========== 복합 조회 (약속 정보 함께) ==========

    /**
     * 약속 공유 정보와 함께 확률 정보 조회
     *
     * JOIN FETCH로 N+1 문제 해결
     *
     * @param shareId 약속 공유 ID
     * @return Optional<ProbEntity> 확률 엔티티 (약속 정보 포함)
     */
    @Query("SELECT p FROM ProbEntity p " +
            "JOIN FETCH p.shareEntity " +
            "WHERE p.shareEntity.shareId = :shareId")
    Optional<ProbEntity> findByShareIdWithShare(@Param("shareId") int shareId);

    /**
     * 모든 확률 정보를 약속 정보와 함께 조회
     *
     * @return List<ProbEntity> 확률 리스트 (약속 정보 포함)
     */
    @Query("SELECT p FROM ProbEntity p JOIN FETCH p.shareEntity")
    List<ProbEntity> findAllWithShare();

    //  LA-11	양 등장 확률 변동	prob_lamb()	양 등장 확률을 생성/수정한다.
    //  → save() 메서드로 probLamb 수정

    //  LA-12	늑대 등장 확률 변동	prob_wolf()	늑대 등장 확률을 생성/수정한다.
    //  → save() 메서드로 probWolf 수정

    //  LA-13	희귀등급 등장 확률 변동	prob_rare()	희귀등급 등장 확률을 생성/수정한다.
    //  → save() 메서드로 probRare 수정
}