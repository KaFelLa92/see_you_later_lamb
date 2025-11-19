package web.repository.promise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.promise.TempEntity;

@Repository
public interface TempRepository extends JpaRepository<TempEntity, Integer> {

    //  PM-08	약속 평가	eval_prom()
}
