package web.repository.promise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.promise.CalendEntity;

@Repository
public interface CalendRepository extends JpaRepository<CalendEntity, Integer> {

    //  PM-09	반복 약속 등록	create_cycle_prom()

    //  PM-10	반복 약속 전체조회	get_cycle_prom()

    //  PM-11	반복 약속 상세조회	get_detail_cycle_prom()

    //  PM-12	반복 약속 수정	update_cycle_prom()


    //  PM-13	반복 약속 삭제	delete_cycle_prom()


}
