package web.repository.promise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.promise.TempEntity;

import java.util.Optional;

@Repository
public interface TempRepository extends JpaRepository<TempEntity, Integer> {

    // PM-08 약속 평가 - 임시 사용자 조회
    Optional<TempEntity> findById(Integer tempId);

    // 추가: 임시 사용자명으로 조회 (중복 확인용)
    Optional<TempEntity> findByTempName(String tempName);
}
