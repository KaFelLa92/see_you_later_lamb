package web.repository.point;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.point.PayEntity;

@Repository
public interface PayRepository extends JpaRepository<PayEntity , Integer> {

    //  AP-05	포인트 지급/삭감	create_point()
}
