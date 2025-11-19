package web.repository.promise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.promise.EvalEntity;

@Repository
public interface EvalRepository extends JpaRepository<EvalEntity, Integer> {

    //  PM-07	약속 공유	share_prom()

    //  PM-08	약속 평가	eval_prom()


}
