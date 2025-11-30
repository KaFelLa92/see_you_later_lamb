package web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.service.PointService;

@RestController
@RequestMapping("api/point")
@RequiredArgsConstructor
public class PointController {
    // [*] DI
    private final PointService pointService;

    //  AP-01	포인트 공식 수정(관)	update_point_policy()

    //  AP-02	포인트 공식 삭제(관)	delete_point_policy()

    //  AP-03	포인트 공식 전체조회(+관)	get_point_policy()

    //  AP-04	포인트 공식 상세조회(+관)	get_detail_point_policy()

    //  AP-05	포인트 지급/삭감	create_point()
}
