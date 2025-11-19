package web.repository.lamb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.lamb.LambEntity;

@Repository
public interface LambRepository extends JpaRepository<LambEntity, Integer> {

    //  AL-01	양 등록(관)	create_lamb()	관리자가 새로운 양을 등록한다.

    //  AL-02	양 수정(관)	update_lamb()

    //  AL-03	양 삭제(관)	delete_lamb()

    //  LA-01	양 전체조회(+관)	get_lamb()	권한에 따라 관리자는 모든 양을, 사용자는 보유한 모든 양을 조회한다.

    //  LA-02	양 상세조회(+관)	get_detail_lamb()

}
