package web.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.model.entity.user.UsersEntity;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity , Integer> {

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



}
