package web.repository.farm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.farm.WorkEntity;

@Repository
public interface WorkRepository extends JpaRepository<WorkEntity, Integer> {

    //  FA-03	업무 전체조회(+관)	get_work()

    //  FA-04	업무 상세조회(+관)	get_detail_work()

    //  FA-07	목장 업무 처리	working_farm() 목장 업무(미니게임) 진행
    //  1) 기한이 지나기 전, 목장 업무(미니게임) 진행 후 work_state랑  work_end_date 갱신
    //  2) 기한 지나면 work_state랑  work_end_date 갱신
}
