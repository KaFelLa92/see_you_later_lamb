package web.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.user.FrenEntity;

@Repository
public interface FrenRepository extends JpaRepository<FrenEntity , Integer> {

    //  FR-01	친구 요청	offer_fren()

    //  FR-02	친구 수락	receive_fren()

    //  FR-03	친구 거절	nagative_fren()

    //  FR-04	친구 전체조회	get_fren()

    //  FR-05	친구 상세조회	get_detail_fren()

    //  FR-06	친구 삭제	delete_fren()

}
