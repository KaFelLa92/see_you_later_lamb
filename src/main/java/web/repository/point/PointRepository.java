package web.repository.point;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.model.entity.point.PointEntity;

import java.util.List;
import java.util.Optional;

/**
 * 포인트 정책(Point Policy) Repository
 * 포인트 적립 공식 데이터베이스 접근을 담당하는 인터페이스
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
public interface PointRepository extends JpaRepository<PointEntity, Integer> {

    // ========== 기본 제공 메서드 (JpaRepository가 자동 제공) ==========
    // save(PointEntity) - 포인트 정책 저장/수정
    // findById(Integer) - ID로 포인트 정책 조회
    // findAll() - 전체 포인트 정책 조회
    // findAll(Pageable) - 페이징 포인트 정책 조회
    // delete(PointEntity) - 포인트 정책 삭제
    // count() - 포인트 정책 개수

    // ========== 커스텀 메서드 정의 ==========

    /**
     * 포인트 정책명으로 조회
     *
     * 생성되는 쿼리:
     * SELECT * FROM point_policy WHERE point_name = ?
     *
     * @param pointName 포인트 정책명
     * @return Optional<PointEntity> 포인트 정책 엔티티
     */
    Optional<PointEntity> findByPointName(String pointName);

    /**
     * 포인트 정책명으로 존재 여부 확인
     *
     * @param pointName 포인트 정책명
     * @return boolean 존재하면 true
     */
    boolean existsByPointName(String pointName);

    /**
     * 포인트 지급량 범위로 정책 조회
     *
     * @param minPoint 최소 포인트
     * @param maxPoint 최대 포인트
     * @return List<PointEntity> 포인트 정책 리스트
     */
    @Query("SELECT p FROM PointEntity p WHERE p.updatePoint BETWEEN :minPoint AND :maxPoint")
    List<PointEntity> findByUpdatePointBetween(@Param("minPoint") int minPoint,
                                               @Param("maxPoint") int maxPoint);

    /**
     * 포인트 지급 정책만 조회 (양수)
     *
     * @return List<PointEntity> 포인트 지급 정책 리스트
     */
    @Query("SELECT p FROM PointEntity p WHERE p.updatePoint > 0")
    List<PointEntity> findPositivePolicies();

    /**
     * 포인트 차감 정책만 조회 (음수)
     *
     * @return List<PointEntity> 포인트 차감 정책 리스트
     */
    @Query("SELECT p FROM PointEntity p WHERE p.updatePoint < 0")
    List<PointEntity> findNegativePolicies();

    /**
     * 포인트 정책명으로 검색 (LIKE 검색, 페이징)
     *
     * @param pointName 검색할 정책명
     * @param pageable 페이징 정보
     * @return Page<PointEntity> 페이징된 검색 결과
     */
    @Query("SELECT p FROM PointEntity p WHERE p.pointName LIKE %:pointName%")
    Page<PointEntity> searchByPointName(@Param("pointName") String pointName, Pageable pageable);

    /**
     * 포인트 지급량으로 정렬하여 조회
     *
     * @param pageable 페이징 정보 (정렬 포함)
     * @return Page<PointEntity> 페이징된 포인트 정책 리스트
     */
    @Query("SELECT p FROM PointEntity p ORDER BY p.updatePoint DESC")
    Page<PointEntity> findAllOrderByUpdatePointDesc(Pageable pageable);

    //  AP-01	포인트 공식 수정(관)	update_point_policy()
    //  → save() 메서드 사용 (ID가 있으면 UPDATE)

    //  AP-02	포인트 공식 삭제(관)	delete_point_policy()
    //  → delete() 메서드 사용

    //  AP-03	포인트 공식 전체조회(+관)	get_point_policy()
    //  → findAll(pageable) 사용

    //  AP-04	포인트 공식 상세조회(+관)	get_detail_point_policy()
    //  → findById() 메서드 사용
}