package web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.service.UserService;

@RestController
@RequestMapping("/seeyoulaterlamb/user")
@RequiredArgsConstructor
public class UserController {
    // [*] DI
    private final UserService userService;



    //  US-01	회원가입	sign_up()

    //  US-02	로그인	login()

    //  US-03	로그아웃	log_out()

    //  US-04	이메일중복검사	check_email()

    //  US-05	연락처중복검사	check_phone()

    //  US-06	이메일 찾기	find_email()

    //  US-07	비밀번호 찾기	find_password()

    //  US-08	내 정보 수정	update_myinfo()

    //  US-09	내 정보 조회	get_myinfo()

    //  US-10	회원 탈퇴	delete_user_state()



    //  AT-01	출석하기	aten()	로그인 된 사용자의 출석일시를 추가한다.

    //  AT-02	출석 조회	get_aten()	로그인 된 사용자의 출석 전체조회



    //  FR-01	친구 요청	offer_fren()

    //  FR-02	친구 수락	receive_fren()

    //  FR-03	친구 거절	nagative_fren()

    //  FR-04	친구 전체조회	get_fren()

    //  FR-05	친구 상세조회	get_detail_fren()

    //  FR-06	친구 삭제	delete_fren()



    //  ST-01	약속 리마인드 설정	set_remind()

    //  ST-02	업무표시 설정	set_work()

    //  ST-03	우선교통수단 설정	set_traffic()

    //  ST-04	언어 설정	set_lang()

    //  ST-05	설정 초기화	set_reset()

}
