package web.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.user.UsersEntity;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity , Integer> {

    //  US-01	회원가입	sign_up()

    //  US-02	로그인	login() - 이메일로 사용자 찾기
    // Optional : 값이 있을 수도 없을 수도 있는 컨테이너 객체
    // findBy필드명 : Spring Data JPA가 자동 쿼리 생성
    Optional <UsersEntity> findByEmail(String email);

    //  US-03	로그아웃	log_out()

    //  US-04	이메일중복검사	check_email() - 이메일 존재 여부 확인
    // existsBy필드명 : boolean 반환 (존재하면 true, 없으면 false)
    boolean existsByEmail(String email);
    
    //  US-05	연락처중복검사	check_phone() - 연락처 존재 여부 확인
    boolean existsByPhone(String phone);

    //  US-06	이메일 찾기	find_email() - 이름과 연락처로 사용자 찾기
    // findBy필드명1And필드명2 : 복수 조건 쿼리
    Optional<UsersEntity> findByUserNameAndPhone(String userName, String phone);

    //  US-07	비밀번호 찾기	find_password()

    //  US-08	내 정보 수정	update_myinfo()

    //  US-09	내 정보 조회	get_myinfo() - finyById 쓰기

    //  US-10	회원 탈퇴	delete_user_state()

    // AU-01	관리자 권한 부여/박탈(관)	update_roll()

}
