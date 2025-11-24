package web.repository.lamb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.lamb.LambCharEntity;

@Repository
public interface LambCharRepository extends JpaRepository<LambCharEntity, Integer> {

    //  AL-04	양 특성 등록(관)	create_lamb_char()

    //  AL-05	양 특성 수정(관)	update_lamb_char()

    //  AL-06	양 특성 삭제(관)	delete_lamb_char()

    //  LA-03	양 특성전체조회(+관)	get_lamb_char()

    //  LA-04	양 특성상세조회(+관)	get_detail_lamb_char()


}
