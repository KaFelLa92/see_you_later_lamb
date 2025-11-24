package web.repository.point;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.point.PointEntity;

@Repository
public interface PointRepository extends JpaRepository<PointEntity , Integer> {

    //  AP-01	포인트 공식 수정(관)	update_point_policy()

    //  AP-02	포인트 공식 삭제(관)	delete_point_policy()

    //  AP-03	포인트 공식 전체조회(+관)	get_point_policy()

    //  AP-04	포인트 공식 상세조회(+관)	get_detail_point_policy()

}
