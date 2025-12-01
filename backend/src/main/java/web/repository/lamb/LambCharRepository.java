package web.repository.lamb;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.model.entity.lamb.LambCharEntity;

import java.util.List;
import java.util.Optional;

/**
 * 양 특성(Lamb Characteristic) Repository
 * 양 특성 데이터베이스 접근을 담당하는 인터페이스
 */
@Repository
public interface LambCharRepository extends JpaRepository<LambCharEntity, Integer> {

    // ========== 기본 제공 메서드 (JpaRepository가 자동 제공) ==========
    // save(LambCharEntity) - 양 특성 저장/수정
    // findById(Integer) - ID로 양 특성 조회
    // findAll() - 전체 양 특성 조회
    // findAll(Pageable) - 페이징 양 특성 조회
    // delete(LambCharEntity) - 양 특성 삭제
    // count() - 양 특성 개수

    // ========== 커스텀 메서드 정의 ==========

    /**
     * 특성명으로 조회
     *
     * 생성되는 쿼리:
     * SELECT * FROM lamb_char WHERE char_name = ?
     *
     * @param charName 특성명
     * @return Optional<LambCharEntity> 양 특성 엔티티
     */
    Optional<LambCharEntity> findByCharName(String charName);

    /**
     * 특성명으로 존재 여부 확인
     *
     * @param charName 특성명
     * @return boolean 존재하면 true
     */
    boolean existsByCharName(String charName);

    /**
     * 활성화된 특성 조회
     *
     * 생성되는 쿼리:
     * SELECT * FROM lamb_char WHERE is_active = ?
     *
     * @param isActive 활성화 여부 (1: 활성화, 0: 비활성화)
     * @return List<LambCharEntity> 양 특성 리스트
     */
    List<LambCharEntity> findByIsActive(int isActive);

    /**
     * 활성화된 특성 조회 (페이징)
     *
     * @param isActive 활성화 여부
     * @param pageable 페이징 정보
     * @return Page<LambCharEntity> 페이징된 양 특성 리스트
     */
    Page<LambCharEntity> findByIsActive(int isActive, Pageable pageable);

    /**
     * 효과 타입으로 조회
     *
     * @param effectType 효과 분류
     * @return List<LambCharEntity> 양 특성 리스트
     */
    List<LambCharEntity> findByEffectType(String effectType);

    /**
     * 특성명으로 검색 (LIKE 검색, 페이징)
     *
     * @param charName 검색할 특성명
     * @param pageable 페이징 정보
     * @return Page<LambCharEntity> 페이징된 검색 결과
     */
    @Query("SELECT lc FROM LambCharEntity lc WHERE lc.charName LIKE %:charName%")
    Page<LambCharEntity> searchByCharName(@Param("charName") String charName, Pageable pageable);

    /**
     * 효과 타입으로 검색 (페이징)
     *
     * @param effectType 효과 분류
     * @param pageable 페이징 정보
     * @return Page<LambCharEntity> 페이징된 검색 결과
     */
    Page<LambCharEntity> findByEffectType(String effectType, Pageable pageable);

    /**
     * 활성화된 특성 개수 조회
     *
     * @param isActive 활성화 여부
     * @return long 개수
     */
    long countByIsActive(int isActive);

    /**
     * 특성명과 활성화 여부로 조회
     *
     * @param charName 특성명
     * @param isActive 활성화 여부
     * @return Optional<LambCharEntity> 양 특성 엔티티
     */
    Optional<LambCharEntity> findByCharNameAndIsActive(String charName, int isActive);

    /**
     * 효과 타입과 활성화 여부로 조회
     *
     * @param effectType 효과 분류
     * @param isActive 활성화 여부
     * @return List<LambCharEntity> 양 특성 리스트
     */
    List<LambCharEntity> findByEffectTypeAndIsActive(String effectType, int isActive);

    //  AL-04	양 특성 등록(관)	create_lamb_char()
    //  → save() 메서드 사용

    //  AL-05	양 특성 수정(관)	update_lamb_char()
    //  → save() 메서드 사용

    //  AL-06	양 특성 삭제(관)	delete_lamb_char()
    //  → delete() 메서드 사용

    //  LA-03	양 특성전체조회(+관)	get_lamb_char()
    //  → findAll() 또는 findByIsActive(1) 사용

    //  LA-04	양 특성상세조회(+관)	get_detail_lamb_char()
    //  → findById() 메서드 사용
}