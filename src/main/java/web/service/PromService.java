package web.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import web.repository.promise.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PromService {
    // [*] DI
    private final PromRepository promRepository;
    private final ShareRepository shareRepository;
    private final CalendRepository calendRepository;
    private final EvalRepository evalRepository;
    private final TempRepository tempRepository;


    //  PM-01	약속 생성	create_prom()

    //  PM-02	약속 수정	update_prom()

    //  PM-03	약속 메모 추가	memo_prom()

    //  PM-04	약속 취소	delete_prom()

    //  PM-05	약속 전체조회(+관)	get_prom()

    //  PM-06	약속 상세조회(+관)	get_detail_prom()

    //  PM-07	약속 공유	share_prom()

    //  PM-08	약속 평가	eval_prom()

    //  PM-09	반복 약속 등록	create_cycle_prom()

    //  PM-10	반복 약속 전체조회	get_cycle_prom()

    //  PM-11	반복 약속 상세조회	get_detail_cycle_prom()

    //  PM-12	반복 약속 수정	update_cycle_prom()

    //  PM-13	반복 약속 삭제	delete_cycle_prom()

}
