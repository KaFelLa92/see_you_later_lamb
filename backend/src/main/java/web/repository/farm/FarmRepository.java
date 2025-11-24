package web.repository.farm;

import org.springframework.data.jpa.repository.JpaRepository;

import web.model.entity.farm.FarmEntity;

public interface FarmRepository extends JpaRepository<FarmEntity, Integer> {

    //  AF-01	목장 등록(관)	create_farm()

    //  AF-02	목장 수정(관)	update_farm()

    //  AF-03	목장 삭제(관)	delete_farm()

    //  FA-01	목장 전체조회(+관)	get_farm()

    //  FA-02	목장 상세조회(+관)	get_detail_farm()

}
