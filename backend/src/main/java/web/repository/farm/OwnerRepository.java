package web.repository.farm;

import org.springframework.data.jpa.repository.JpaRepository;

import web.model.entity.farm.OwnerEntity;

public interface OwnerRepository extends JpaRepository<OwnerEntity, Integer> {

    //  FA-01	목장 전체조회(+관)	get_farm()

    //  FA-02	목장 상세조회(+관)	get_detail_farm()

    //  FA-05	목장 구매	buy_farm()

    //  FA-06	목장 이름 짓기	naming_farm()

    //  FA-07	목장 업무 처리	working_farm()

}
