package web.repository.lamb;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.model.entity.common.LambRank;
import web.model.entity.lamb.LambEntity;

import java.util.List;
import java.util.Optional;

/**
 * 양(Lamb) Repository
 * 양 정보 데이터베이스 접근을 담당하는 인터페이스
 *
 * JpaRepository<Entity타입, PK타입>를 상속받으면 기본적인 CRUD 메서드가 자동 제공됨:
 * - save(entity): 저장/수정
 * - findById(id): ID로 조회
 * - findAll(): 전체 조회
 * - findAll(pageable): 페이징 조회
 * - delete(entity): 삭제
 * - count(): 개수 조회
 * 등
 */
@Repository
public interface LambRepository extends JpaRepository<LambEntity, Integer> {

    // ========== 기본 제공 메서드 (JpaRepository가 자동 제공) ==========
    // save(LambEntity) - 양 저장/수정
    // findById(Integer) - ID로 양 조회
    // findAll() - 전체 양 조회
    // findAll(Pageable) - 페이징 양 조회
    // delete(LambEntity) - 양 삭제
    // count() - 양 개수

    // ========== 커스텀 메서드 정의 ==========

    /**
     * 양 품종명으로 조회
     *
     * 메서드 이름 규칙:
     * - findBy + 필드명 = 해당 필드로 조회
     * - Spring Data JPA가 자동으로 쿼리 생성
     *
     * 생성되는 쿼리:
     * SELECT * FROM lamb_info WHERE lamb_name = ?
     *
     * @param lambName 양 품종명
     * @return Optional<LambEntity> 양 엔티티 (없으면 empty)
     */
    Optional<LambEntity> findByLambName(String lambName);

    /**
     * 양 등급으로 조회 (페이징)
     *
     * 생성되는 쿼리:
     * SELECT * FROM lamb_info WHERE lamb_rank = ? LIMIT ? OFFSET ?
     *
     * @param lambRank 양 등급
     * @param pageable 페이징 정보 (페이지 번호, 사이즈, 정렬)
     * @return Page<LambEntity> 페이징된 양 리스트
     */
    Page<LambEntity> findByLambRank(LambRank lambRank, Pageable pageable);

    /**
     * 양 등급으로 조회 (리스트)
     *
     * @param lambRank 양 등급
     * @return List<LambEntity> 양 리스트
     */
    List<LambEntity> findByLambRank(LambRank lambRank);

    /**
     * 특정 특성을 가진 양 조회
     *
     * 연관 관계 탐색:
     * - lambCharEntity.charId로 접근
     *
     * 생성되는 쿼리:
     * SELECT * FROM lamb_info WHERE char_id = ?
     *
     * @param charId 양 특성 ID
     * @return List<LambEntity> 양 리스트
     */
    List<LambEntity> findByLambCharEntity_CharId(int charId);

    /**
     * 양 품종명으로 존재 여부 확인
     *
     * 생성되는 쿼리:
     * SELECT COUNT(*) > 0 FROM lamb_info WHERE lamb_name = ?
     *
     * @param lambName 양 품종명
     * @return boolean 존재하면 true
     */
    boolean existsByLambName(String lambName);

    /**
     * 양 등급별 개수 조회
     *
     * @param lambRank 양 등급
     * @return long 개수
     */
    long countByLambRank(LambRank lambRank);

    /**
     * 양 품종명으로 검색 (LIKE 검색, 페이징)
     *
     * @Query 어노테이션 사용:
     * - JPQL(Java Persistence Query Language) 작성
     * - :lambName은 파라미터 바인딩
     *
     * JPQL에서는 테이블명이 아닌 Entity명 사용:
     * - FROM LambEntity (테이블명 lamb_info가 아님)
     *
     * @param lambName 검색할 품종명
     * @param pageable 페이징 정보
     * @return Page<LambEntity> 페이징된 검색 결과
     */
    @Query("SELECT l FROM LambEntity l WHERE l.lambName LIKE %:lambName%")
    Page<LambEntity> searchByLambName(@Param("lambName") String lambName, Pageable pageable);

    /**
     * 등급 범위로 양 조회
     *
     * JPQL에서 IN 절 사용:
     * - 여러 등급을 한번에 조회
     *
     * @param ranks 등급 리스트
     * @param pageable 페이징 정보
     * @return Page<LambEntity> 페이징된 양 리스트
     */
    @Query("SELECT l FROM LambEntity l WHERE l.lambRank IN :ranks")
    Page<LambEntity> findByLambRankIn(@Param("ranks") List<LambRank> ranks, Pageable pageable);

    /**
     * 특정 특성을 가진 양 개수 조회
     *
     * @param charId 양 특성 ID
     * @return long 개수
     */
    @Query("SELECT COUNT(l) FROM LambEntity l WHERE l.lambCharEntity.charId = :charId")
    long countByCharId(@Param("charId") int charId);

    /**
     * 양 전체 조회 (특성 정보 함께 가져오기)
     *
     * JOIN FETCH:
     * - Lazy Loading을 즉시 로딩으로 변경
     * - N+1 문제 해결
     * - 한 번의 쿼리로 양과 특성 정보를 모두 가져옴
     *
     * @param pageable 페이징 정보
     * @return Page<LambEntity> 페이징된 양 리스트 (특성 정보 포함)
     */
    @Query("SELECT l FROM LambEntity l JOIN FETCH l.lambCharEntity")
    Page<LambEntity> findAllWithChar(Pageable pageable);

    // ========== 주석으로 설명하는 JPA 메서드 명명 규칙 ==========
    /*
     * JPA Repository 메서드 명명 규칙:
     *
     * 1. 조회 (SELECT)
     *    - findBy필드명: 조회
     *    - findAllBy필드명: 전체 조회
     *    - getBy필드명: 조회 (findBy와 동일)
     *
     * 2. 존재 여부 (EXISTS)
     *    - existsBy필드명: boolean 반환
     *
     * 3. 개수 (COUNT)
     *    - countBy필드명: long 반환
     *
     * 4. 삭제 (DELETE)
     *    - deleteBy필드명: void 또는 long(삭제된 개수) 반환
     *
     * 5. 조건 추가
     *    - And: findByField1AndField2
     *    - Or: findByField1OrField2
     *    - Between: findByFieldBetween
     *    - LessThan: findByFieldLessThan
     *    - GreaterThan: findByFieldGreaterThan
     *    - Like: findByFieldLike
     *    - In: findByFieldIn
     *    - IsNull: findByFieldIsNull
     *    - IsNotNull: findByFieldIsNotNull
     *
     * 6. 정렬
     *    - OrderBy필드명Asc: 오름차순
     *    - OrderBy필드명Desc: 내림차순
     *
     * 예시:
     * - findByLambNameAndLambRank(String name, LambRank rank)
     * - findByLambRankOrderByLambNameAsc(LambRank rank)
     * - findByLambNameLike(String name)
     */

    //  AL-01	양 등록(관)	create_lamb()	관리자가 새로운 양을 등록한다.
    //  → save() 메서드 사용

    //  AL-02	양 수정(관)	update_lamb()
    //  → save() 메서드 사용 (ID가 있으면 UPDATE)

    //  AL-03	양 삭제(관)	delete_lamb()
    //  → delete() 메서드 사용

    //  LA-01	양 전체조회(+관)	get_lamb()	권한에 따라 관리자는 모든 양을, 사용자는 보유한 모든 양을 조회한다.
    //  → findAll(pageable) 또는 ShepRepository의 findByUsersEntity_UserId() 사용

    //  LA-02	양 상세조회(+관)	get_detail_lamb()
    //  → findById() 메서드 사용
}