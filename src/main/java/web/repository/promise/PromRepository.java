package web.repository.promise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.promise.PromEntity;
import web.model.entity.user.UsersEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromRepository extends JpaRepository<PromEntity, Integer> {

    //  PM-01	약속 생성	create_prom()

    //  PM-02	약속 수정	update_prom()

    //  PM-03	약속 메모 추가	memo_prom()

    //  PM-04	약속 취소	delete_prom()

    // PM-05 약속 전체조회 - 특정 사용자의 모든 약속
    // findBy + 필드명: Spring Data JPA가 자동으로 쿼리 생성
    List<PromEntity> findByUsersEntity(UsersEntity usersEntity);

    // PM-05 약속 전체조회 - 특정 사용자의 약속을 날짜 범위로 조회
    // Between: SQL의 BETWEEN과 동일
    List<PromEntity> findByUsersEntityAndPromDateBetween(
            UsersEntity usersEntity,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // PM-06 약속 상세조회 - ID로 약속 조회
    // findById는 JpaRepository가 기본 제공하므로 선언 불필요
    // Optional<PromEntity> findById(Integer promId);

    // 추가: 약속 날짜 순으로 정렬 조회
    // OrderBy + 필드명 + Asc/Desc: 정렬 조건
    List<PromEntity> findByUsersEntityOrderByPromDateAsc(UsersEntity usersEntity);

    // 추가: 특정 날짜 이후의 약속만 조회 (지나간 약속 제외)
    // After: 특정 값 이후
    List<PromEntity> findByUsersEntityAndPromDateAfter(
            UsersEntity usersEntity,
            LocalDateTime date
    );

}
