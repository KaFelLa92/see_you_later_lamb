package web.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.user.SetEntity;
import web.model.entity.user.UsersEntity;

import java.util.Optional;

@Repository
public interface SetRepository extends JpaRepository<SetEntity, Integer> {

    //  ST-01	약속 리마인드 설정	set_remind()

    //  ST-02	업무표시 설정	set_work()

    //  ST-03	우선교통수단 설정	set_traffic()

    //  ST-04	언어 설정	set_lang()

    //  ST-05	설정 초기화	set_reset()

    // 특정 사용자의 설정 조회
    // 사용자당 설정은 하나만 존재하므로 user_id로 검색
    Optional<SetEntity> findByUsersEntity(UsersEntity usersEntity);

    // 특정 사용자의 설정 존재 여부 확인
    boolean existsByUsersEntity(UsersEntity usersEntity);

}
