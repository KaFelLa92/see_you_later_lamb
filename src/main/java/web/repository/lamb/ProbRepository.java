package web.repository.lamb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.lamb.ProbEntity;

@Repository
public interface ProbRepository extends JpaRepository<ProbEntity, Integer> {

    //  LA-11	양 등장 확률 변동	prob_lamb()	양 등장 확률을 생성/수정한다.

    //  LA-12	늑대 등장 확률 변동	prob_wolf()	늑대 등장 확률을 생성/수정한다.

    //  LA-13	희귀등급 등장 확률 변동	prob_rare()	희귀등급 등장 확률을 생성/수정한다.
}
