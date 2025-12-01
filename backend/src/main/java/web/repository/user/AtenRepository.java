package web.repository.user;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.user.AtenEntity;
import web.model.entity.user.UsersEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AtenRepository extends JpaRepository<AtenEntity, Integer> {

    //  AT-01	출석하기	aten()	로그인 된 사용자의 출석일시를 추가한다.
    // 오늘 이미 출석했는지 확인용 : 같은 날짜에 중복 출석 방지
    Optional<AtenEntity> findByUsersEntityAndAtenDate(UsersEntity usersEntity, LocalDate atenDate);

    //  AT-02	출석 조회	get_aten()	로그인 된 사용자의 출석 전체조회
    // findBy엔티티명 : 외래키로 조회
    // List 반환 : 여러개의 결과 반환
    List<AtenEntity> findByUsersEntity(UsersEntity usersEntity);

    // [*] 특정 사용자의 출석 기록 개수 조회 (출석일수 확인용)
    long countByUsersEntity(UsersEntity usersEntity);

    /**
     * 특정 사용자의 출석 기록 조회
     *
     * @param userId 사용자 ID
     * @return List<AtenEntity> 출석 기록 리스트
     */
    List<AtenEntity> findByUsersEntity_UserId(int userId);

}
