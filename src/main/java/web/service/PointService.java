package web.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import web.repository.point.PayRepository;
import web.repository.point.PointRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {
    // [*] DI
    private final PointRepository pointRepository;
    private final PayRepository payRepository;

    //  AP-01	포인트 공식 수정(관)	update_point_policy()

    //  AP-02	포인트 공식 삭제(관)	delete_point_policy()

    //  AP-03	포인트 공식 전체조회(+관)	get_point_policy()

    //  AP-04	포인트 공식 상세조회(+관)	get_detail_point_policy()

    //  AP-05	포인트 지급/삭감	create_point()

}
