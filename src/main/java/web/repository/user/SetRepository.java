package web.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.user.SetEntity;

@Repository
public interface SetRepository extends JpaRepository<SetEntity, Integer> {

    //  ST-01	약속 리마인드 설정	set_remind()

    //  ST-02	업무표시 설정	set_work()

    //  ST-03	우선교통수단 설정	set_traffic()

    //  ST-04	언어 설정	set_lang()

    //  ST-05	설정 초기화	set_reset()

}
