package web.repository.promise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.promise.PromEntity;

@Repository
public interface PromRepository extends JpaRepository<PromEntity, Integer> {

    //  PM-01	약속 생성	create_prom()

    //  PM-02	약속 수정	update_prom()

    //  PM-03	약속 메모 추가	memo_prom()

    //  PM-04	약속 취소	delete_prom()

    //  PM-05	약속 전체조회(+관)	get_prom()

    //  PM-06	약속 상세조회(+관)	get_detail_prom()

}
