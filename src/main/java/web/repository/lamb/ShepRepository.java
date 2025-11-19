package web.repository.lamb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.lamb.ShepEntity;

@Repository
public interface ShepRepository extends JpaRepository<ShepEntity, Integer> {

    //  LA-01	양 전체조회(+관)	get_lamb()	권한에 따라 관리자는 모든 양을, 사용자는 보유한 모든 양을 조회한다.

    //  LA-02	양 상세조회(+관)	get_detail_lamb()

    //  LA-03	양 특성전체조회(+관)	get_lamb_char()

    //  LA-04	양 특성상세조회(+관)	get_detail_lamb_char()

    //  LA-05	양 이름 짓기	naming_lamb()

    //  LA-06	양 밥 주기	feeding_lamb()

    //  LA-07	양 털 깎기	shaving_lamb()

    //  LA-08	양 장소 옮기기		moving_lamb() 양을 울타리에서 목장, 또는 목장에서 울타리로 옮긴다.

    //  LA-09	양 등장    appear_lamb()

    //  LA-10	양 실종    missing_lamb()
}
