package web.repository.lamb;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.model.entity.lamb.ShepEntity;

import java.util.List;
import java.util.Optional;

/**
 * 양치기(Shepherd) Repository
 * 사용자가 키우는 양의 데이터베이스 접근을 담당하는 인터페이스
 */
@Repository
public interface ShepRepository extends JpaRepository<ShepEntity, Integer> {

    // ========== 기본 제공 메서드 (JpaRepository가 자동 제공) ==========
    // save(ShepEntity) - 양치기 저장/수정
    // findById(Integer) - ID로 양치기 조회
    // findAll() - 전체 양치기 조회
    // delete(ShepEntity) - 양치기 삭제

    // ========== 사용자별 양 조회 ==========

    /**
     * 특정 사용자의 모든 양 조회
     *
     * 연관 관계 탐색:
     * - usersEntity.userId로 접근
     *
     * 생성되는 쿼리:
     * SELECT * FROM shepherd WHERE user_id = ?
     *
     * @param userId 사용자 ID
     * @return List<ShepEntity> 양 리스트
     */
    List<ShepEntity> findByUsersEntity_UserId(int userId);

    /**
     * 특정 사용자의 모든 양 조회 (페이징)
     *
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return Page<ShepEntity> 페이징된 양 리스트
     */
    Page<ShepEntity> findByUsersEntity_UserId(int userId, Pageable pageable);

    /**
     * 특정 사용자의 양 개수 조회
     *
     * @param userId 사용자 ID
     * @return long 양 개수
     */
    long countByUsersEntity_UserId(int userId);

    // ========== 양 상태별 조회 ==========

    /**
     * 특정 사용자의 특정 위치에 있는 양 조회
     *
     * shepExist:
     * - 1: 울타리에 있는 양
     * - 0: 목장에 있는 양
     * - -1: 늑대에게 쫓기는 양
     *
     * @param userId 사용자 ID
     * @param shepExist 양 존재 여부
     * @return List<ShepEntity> 양 리스트
     */
    List<ShepEntity> findByUsersEntity_UserIdAndShepExist(int userId, int shepExist);

    /**
     * 특정 사용자의 울타리에 있는 양 조회
     *
     * @param userId 사용자 ID
     * @return List<ShepEntity> 울타리에 있는 양 리스트
     */
    @Query("SELECT s FROM ShepEntity s WHERE s.usersEntity.userId = :userId AND s.shepExist = 1")
    List<ShepEntity> findUserLambsInFence(@Param("userId") int userId);

    /**
     * 특정 사용자의 목장에 있는 양 조회
     *
     * @param userId 사용자 ID
     * @return List<ShepEntity> 목장에 있는 양 리스트
     */
    @Query("SELECT s FROM ShepEntity s WHERE s.usersEntity.userId = :userId AND s.shepExist = 0")
    List<ShepEntity> findUserLambsInFarm(@Param("userId") int userId);

    /**
     * 특정 사용자의 배고픈 양 조회
     *
     * shepHunger:
     * - -1: 배고픔
     * - 0: 보통
     * - 1: 배부름
     *
     * @param userId 사용자 ID
     * @return List<ShepEntity> 배고픈 양 리스트
     */
    @Query("SELECT s FROM ShepEntity s WHERE s.usersEntity.userId = :userId AND s.shepHunger = -1")
    List<ShepEntity> findHungryLambs(@Param("userId") int userId);

    /**
     * 특정 사용자의 털이 많은 양 조회
     *
     * shepFur:
     * - -1: 털 많음
     * - 0: 털 보통
     * - 1: 털 없음
     *
     * @param userId 사용자 ID
     * @return List<ShepEntity> 털이 많은 양 리스트
     */
    @Query("SELECT s FROM ShepEntity s WHERE s.usersEntity.userId = :userId AND s.shepFur = -1")
    List<ShepEntity> findFurryLambs(@Param("userId") int userId);

    // ========== 양 품종별 조회 ==========

    /**
     * 특정 사용자가 특정 품종의 양을 보유하고 있는지 확인
     *
     * @param userId 사용자 ID
     * @param lambId 양 품종 ID
     * @return boolean 보유하고 있으면 true
     */
    boolean existsByUsersEntity_UserIdAndLambEntity_LambId(int userId, int lambId);

    /**
     * 특정 사용자의 특정 품종 양 조회
     *
     * @param userId 사용자 ID
     * @param lambId 양 품종 ID
     * @return List<ShepEntity> 양 리스트
     */
    List<ShepEntity> findByUsersEntity_UserIdAndLambEntity_LambId(int userId, int lambId);

    /**
     * 특정 품종의 양을 키우는 사용자 수 조회
     *
     * @param lambId 양 품종 ID
     * @return long 사용자 수
     */
    long countByLambEntity_LambId(int lambId);

    // ========== 양 검색 ==========

    /**
     * 특정 사용자의 양을 이름으로 검색
     *
     * @param userId 사용자 ID
     * @param shepName 검색할 양 이름
     * @param pageable 페이징 정보
     * @return Page<ShepEntity> 페이징된 검색 결과
     */
    @Query("SELECT s FROM ShepEntity s WHERE s.usersEntity.userId = :userId AND s.shepName LIKE %:shepName%")
    Page<ShepEntity> searchByUserIdAndShepName(@Param("userId") int userId,
                                               @Param("shepName") String shepName,
                                               Pageable pageable);

    // ========== 복합 조회 (양과 사용자 정보 함께) ==========

    /**
     * 특정 사용자의 양을 품종 정보와 함께 조회
     *
     * JOIN FETCH:
     * - Lazy Loading을 즉시 로딩으로 변경
     * - N+1 문제 해결
     *
     * @param userId 사용자 ID
     * @return List<ShepEntity> 양 리스트 (품종 정보 포함)
     */
    @Query("SELECT s FROM ShepEntity s " +
            "JOIN FETCH s.lambEntity " +
            "WHERE s.usersEntity.userId = :userId")
    List<ShepEntity> findByUserIdWithLamb(@Param("userId") int userId);

    /**
     * 특정 사용자의 양을 품종 정보와 함께 조회 (페이징)
     *
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return Page<ShepEntity> 페이징된 양 리스트 (품종 정보 포함)
     */
    @Query(value = "SELECT s FROM ShepEntity s " +
            "JOIN FETCH s.lambEntity " +
            "WHERE s.usersEntity.userId = :userId",
            countQuery = "SELECT COUNT(s) FROM ShepEntity s WHERE s.usersEntity.userId = :userId")
    Page<ShepEntity> findByUserIdWithLamb(@Param("userId") int userId, Pageable pageable);

    /**
     * 특정 양치기 ID와 사용자 ID로 조회 (소유권 확인용)
     *
     * @param shepId 양치기 ID
     * @param userId 사용자 ID
     * @return Optional<ShepEntity> 양치기 엔티티
     */
    Optional<ShepEntity> findByShepIdAndUsersEntity_UserId(int shepId, int userId);

    //  LA-01	양 전체조회(+관)	get_lamb()	권한에 따라 관리자는 모든 양을, 사용자는 보유한 모든 양을 조회한다.
    //  → findAll() 또는 findByUsersEntity_UserId() 사용

    //  LA-02	양 상세조회(+관)	get_detail_lamb()
    //  → findById() 또는 findByShepIdAndUsersEntity_UserId() 사용

    //  LA-05	양 이름 짓기	naming_lamb()
    //  → save() 메서드로 shepName 수정

    //  LA-06	양 밥 주기	feeding_lamb()
    //  → save() 메서드로 shepHunger 수정

    //  LA-07	양 털 깎기	shaving_lamb()
    //  → save() 메서드로 shepFur 수정

    //  LA-08	양 장소 옮기기	moving_lamb()
    //  → save() 메서드로 shepExist 수정

    //  LA-09	양 등장    appear_lamb()
    //  → save() 메서드로 새 ShepEntity 생성

    //  LA-10	양 실종    missing_lamb()
    //  → save() 메서드로 shepExist를 -1로 수정 또는 delete()
}