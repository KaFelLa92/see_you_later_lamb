package web.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import web.repository.farm.FarmRepository;
import web.repository.farm.OwnerRepository;
import web.repository.farm.WorkRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class FarmService {
    // [*] DI
    private final FarmRepository farmRepository;
    private final OwnerRepository ownerRepository;
    private final WorkRepository workRepository;


    //  AF-01	목장 등록(관)	create_farm()

    //  AF-02	목장 수정(관)	update_farm()

    //  AF-03	목장 삭제(관)	delete_farm()

    //  FA-01	목장 전체조회(+관)	get_farm()

    //  FA-02	목장 상세조회(+관)	get_detail_farm()

    //  FA-03	업무 전체조회(+관)	get_work()

    //  FA-04	업무 상세조회(+관)	get_detail_work()

    //  FA-05	목장 구매	buy_farm()

    //  FA-06	목장 이름 짓기	naming_farm()

    //  FA-07	목장 업무 처리	working_farm() 목장 업무(미니게임) 진행
    //  1) 기한이 지나기 전, 목장 업무(미니게임) 진행 후 work_state랑  work_end_date 갱신
    //  2) 기한 지나면 work_state랑  work_end_date 갱신


}
