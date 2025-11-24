package web.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import web.repository.lamb.LambCharRepository;
import web.repository.lamb.LambRepository;
import web.repository.lamb.ProbRepository;
import web.repository.lamb.ShepRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class LambService {
    // [*] DI
    private final LambRepository lambRepository;
    private final LambCharRepository lambCharRepository;
    private final ProbRepository probRepository;
    private final ShepRepository shepRepository;

    //  AL-01	양 등록(관)	create_lamb()	관리자가 새로운 양을 등록한다.

    //  AL-02	양 수정(관)	update_lamb()

    //  AL-03	양 삭제(관)	delete_lamb()

    //  AL-04	양 특성 등록(관)	create_lamb_char()

    //  AL-05	양 특성 수정(관)	update_lamb_char()

    //  AL-06	양 특성 삭제(관)	delete_lamb_char()



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

    //  LA-11	양 등장 확률 변동	prob_lamb()	양 등장 확률을 생성/수정한다.

    //  LA-12	늑대 등장 확률 변동	prob_wolf()	늑대 등장 확률을 생성/수정한다.

    //  LA-13	희귀등급 등장 확률 변동	prob_rare()	희귀등급 등장 확률을 생성/수정한다.


}
