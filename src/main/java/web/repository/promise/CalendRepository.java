package web.repository.promise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.common.CycleType;
import web.model.entity.promise.CalendEntity;
import web.model.entity.promise.PromEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalendRepository extends JpaRepository<CalendEntity, Integer> {

    //  PM-09	반복 약속 등록	create_cycle_prom()

    // PM-10 반복 약속 전체조회 - 특정 약속의 모든 반복 일정
    List<CalendEntity> findByPromEntity(PromEntity promEntity);

    // PM-11 반복 약속 상세조회 - ID로 반복 일정 조회
    Optional<CalendEntity> findById(Integer calendId);

    // 추가: 특정 약속의 특정 반복 주기 조회
    List<CalendEntity> findByPromEntityAndCalendCycle(
            PromEntity promEntity,
            CycleType cycleType
    );

    // 추가: 특정 약속에 반복 일정이 있는지 확인
    boolean existsByPromEntity(PromEntity promEntity);

    //  PM-12	반복 약속 수정	update_cycle_prom()


    //  PM-13	반복 약속 삭제	delete_cycle_prom()


}
