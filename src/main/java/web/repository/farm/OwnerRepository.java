package web.repository.farm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.model.entity.farm.OwnerEntity;

import java.util.List;
import java.util.Optional;

/**
 * 목장주(Farm Owner) Repository
 * 사용자가 소유한 목장 데이터베이스 접근을 담당하는 인터페이스
 */
@Repository
public interface OwnerRepository extends JpaRepository<OwnerEntity, Integer> {

    // ========== 기본 제공 메서드 (JpaRepository가 자동 제공) ==========
    // save(OwnerEntity) - 목장주 저장/수정
    // findById(Integer) - ID로 목장주 조회
    // findAll() - 전체 목장주 조회
    // delete(OwnerEntity) - 목장주 삭제

    // ========== 사용자별 목장 조회 ==========

    /**
     * 특정 사용자의 모든 목장 조회
     *
     * 연관 관계 탐색:
     * - usersEntity.userId로 접근
     *
     * 생성되는 쿼리:
     * SELECT * FROM farm_owner WHERE user_id = ?
     *
     * @param userId 사용자 ID
     * @return List<OwnerEntity> 목장주 리스트
     */
    List<OwnerEntity> findByUsersEntity_UserId(int userId);

    /**
     * 특정 사용자의 모든 목장 조회 (페이징)
     *
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return Page<OwnerEntity> 페이징된 목장주 리스트
     */
    Page<OwnerEntity> findByUsersEntity_UserId(int userId, Pageable pageable);

    /**
     * 특정 사용자의 목장 개수 조회
     *
     * @param userId 사용자 ID
     * @return long 목장 개수
     */
    long countByUsersEntity_UserId(int userId);

    // ========== 목장 유형별 조회 ==========

    /**
     * 특정 사용자가 특정 목장 유형을 보유하고 있는지 확인
     *
     * @param userId 사용자 ID
     * @param farmId 목장 유형 ID
     * @return boolean 보유하고 있으면 true
     */
    boolean existsByUsersEntity_UserIdAndFarmEntity_FarmId(int userId, int farmId);

    /**
     * 특정 사용자의 특정 목장 유형 조회
     *
     * @param userId 사용자 ID
     * @param farmId 목장 유형 ID
     * @return Optional<OwnerEntity> 목장주 엔티티
     */
    Optional<OwnerEntity> findByUsersEntity_UserIdAndFarmEntity_FarmId(int userId, int farmId);

    /**
     * 특정 목장 유형을 소유한 사용자 수 조회
     *
     * @param farmId 목장 유형 ID
     * @return long 사용자 수
     */
    long countByFarmEntity_FarmId(int farmId);

    // ========== 목장 검색 ==========

    /**
     * 특정 사용자의 목장을 이름으로 검색
     *
     * @param userId 사용자 ID
     * @param ownerName 검색할 목장 이름
     * @param pageable 페이징 정보
     * @return Page<OwnerEntity> 페이징된 검색 결과
     */
    @Query("SELECT o FROM OwnerEntity o WHERE o.usersEntity.userId = :userId AND o.ownerName LIKE %:ownerName%")
    Page<OwnerEntity> searchByUserIdAndOwnerName(@Param("userId") int userId,
                                                 @Param("ownerName") String ownerName,
                                                 Pageable pageable);

    // ========== 복합 조회 (목장 정보와 함께) ==========

    /**
     * 특정 사용자의 목장을 목장 정보와 함께 조회
     *
     * JOIN FETCH:
     * - Lazy Loading을 즉시 로딩으로 변경
     * - N+1 문제 해결
     *
     * @param userId 사용자 ID
     * @return List<OwnerEntity> 목장주 리스트 (목장 정보 포함)
     */
    @Query("SELECT o FROM OwnerEntity o " +
            "JOIN FETCH o.farmEntity " +
            "WHERE o.usersEntity.userId = :userId")
    List<OwnerEntity> findByUserIdWithFarm(@Param("userId") int userId);

    /**
     * 특정 사용자의 목장을 목장 정보와 함께 조회 (페이징)
     *
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return Page<OwnerEntity> 페이징된 목장주 리스트 (목장 정보 포함)
     */
    @Query(value = "SELECT o FROM OwnerEntity o " +
            "JOIN FETCH o.farmEntity " +
            "WHERE o.usersEntity.userId = :userId",
            countQuery = "SELECT COUNT(o) FROM OwnerEntity o WHERE o.usersEntity.userId = :userId")
    Page<OwnerEntity> findByUserIdWithFarm(@Param("userId") int userId, Pageable pageable);

    /**
     * 특정 목장주 ID와 사용자 ID로 조회 (소유권 확인용)
     *
     * @param ownerId 목장주 ID
     * @param userId 사용자 ID
     * @return Optional<OwnerEntity> 목장주 엔티티
     */
    Optional<OwnerEntity> findByOwnerIdAndUsersEntity_UserId(int ownerId, int userId);

    //  FA-01	목장 전체조회(+관)	get_farm()
    //  → findAll() 또는 findByUsersEntity_UserId() 사용

    //  FA-02	목장 상세조회(+관)	get_detail_farm()
    //  → findById() 또는 findByOwnerIdAndUsersEntity_UserId() 사용

    //  FA-05	목장 구매	buy_farm()
    //  → save() 메서드로 새 OwnerEntity 생성

    //  FA-06	목장 이름 짓기	naming_farm()
    //  → save() 메서드로 ownerName 수정

    //  FA-07	목장 업무 처리	working_farm()
    //  → WorkRepository 사용
}