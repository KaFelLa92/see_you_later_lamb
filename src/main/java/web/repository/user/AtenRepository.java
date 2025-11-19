package web.repository.user;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.user.AtenEntity;

@Repository
public interface AtenRepository extends JpaRepository<AtenEntity, Integer> {

    //  AT-01	출석하기	aten()	로그인 된 사용자의 출석일시를 추가한다.

    //  AT-02	출석 조회	get_aten()	로그인 된 사용자의 출석 전체조회
}
