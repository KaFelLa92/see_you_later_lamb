package web.repository.farm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.model.entity.farm.FarmEntity;

import java.util.List;
import java.util.Optional;

/**
 * 목장(Farm) Repository
 * 목장 정보 데이터베이스 접근을 담당하는 인터페이스
 *
 * JpaRepository<Entity타입, PK타입>를 상속받으면 기본적인 CRUD 메서드가 자동 제공됨:
 * - save(entity): 저장/수정
 * - findById(id): ID로 조회
 * - findAll(): 전체 조회
 * - findAll(pageable): 페이징 조회
 * - delete(entity): 삭제
 * - count(): 개수 조회
 */
@Repository
public interface FarmRepository extends JpaRepository<FarmEntity, Integer> {

    // ========== 기본 제공 메서드 (JpaRepository가 자동 제공) ==========
    // save(FarmEntity) - 목장 저장/수정
    // findById(Integer) - ID로 목장 조회
    // findAll() - 전체 목장 조회
    // findAll(Pageable) - 페이징 목장 조회
    // delete(FarmEntity) - 목장 삭제
    // count() - 목장 개수

    // ========== 커스텀 메서드 정의 ==========

    /**
     * 목장명으로 조회
     *
     * 생성되는 쿼리:
     * SELECT * FROM farm_info WHERE farm_name = ?
     *
     * @param farmName 목장명
     * @return Optional<FarmEntity> 목장 엔티티
     */
    Optional<FarmEntity> findByFarmName(String farmName);

    /**
     * 목장명으로 존재 여부 확인
     *
     * @param farmName 목장명
     * @return boolean 존재하면 true
     */
    boolean existsByFarmName(String farmName);

    /**
     * 구매 비용 범위로 목장 조회 (페이징)
     *
     * @param minCost 최소 비용
     * @param maxCost 최대 비용
     * @param pageable 페이징 정보
     * @return Page<FarmEntity> 페이징된 목장 리스트
     */
    @Query("SELECT f FROM FarmEntity f WHERE f.farmCost BETWEEN :minCost AND :maxCost")
    Page<FarmEntity> findByFarmCostBetween(@Param("minCost") int minCost,
                                           @Param("maxCost") int maxCost,
                                           Pageable pageable);

    /**
     * 최대 양 수 기준으로 목장 조회
     *
     * @param minLamb 최소 양 수
     * @return List<FarmEntity> 목장 리스트
     */
    @Query("SELECT f FROM FarmEntity f WHERE f.maxLamb >= :minLamb")
    List<FarmEntity> findByMaxLambGreaterThanEqual(@Param("minLamb") int minLamb);

    /**
     * 목장명으로 검색 (LIKE 검색, 페이징)
     *
     * @param farmName 검색할 목장명
     * @param pageable 페이징 정보
     * @return Page<FarmEntity> 페이징된 검색 결과
     */
    @Query("SELECT f FROM FarmEntity f WHERE f.farmName LIKE %:farmName%")
    Page<FarmEntity> searchByFarmName(@Param("farmName") String farmName, Pageable pageable);

    /**
     * 특정 사용자가 소유한 목장 유형 조회
     *
     * @param userId 사용자 ID
     * @return List<FarmEntity> 목장 리스트
     */
    @Query("SELECT DISTINCT f FROM FarmEntity f " +
            "JOIN OwnerEntity o ON o.farmEntity.farmId = f.farmId " +
            "WHERE o.usersEntity.userId = :userId")
    List<FarmEntity> findFarmsByUserId(@Param("userId") int userId);

    /**
     * 구매 비용이 특정 금액 이하인 목장 조회
     *
     * @param maxCost 최대 구매 비용
     * @param pageable 페이징 정보
     * @return Page<FarmEntity> 페이징된 목장 리스트
     */
    Page<FarmEntity> findByFarmCostLessThanEqual(int maxCost, Pageable pageable);

    //  AF-01	목장 등록(관)	create_farm()
    //  → save() 메서드 사용

    //  AF-02	목장 수정(관)	update_farm()
    //  → save() 메서드 사용 (ID가 있으면 UPDATE)

    //  AF-03	목장 삭제(관)	delete_farm()
    //  → delete() 메서드 사용

    //  FA-01	목장 전체조회(+관)	get_farm()
    //  → findAll(pageable) 사용

    //  FA-02	목장 상세조회(+관)	get_detail_farm()
    //  → findById() 메서드 사용
}