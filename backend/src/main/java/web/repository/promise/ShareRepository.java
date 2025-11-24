package web.repository.promise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import web.model.entity.promise.PromEntity;
import web.model.entity.promise.ShareEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShareRepository extends JpaRepository<ShareEntity, Integer> {

    // PM-07 약속 공유 - 특정 약속의 모든 공유 조회
    List<ShareEntity> findByPromEntity(PromEntity promEntity);

    // PM-08 약속 평가 - ID로 공유 조회
    Optional<ShareEntity> findById(Integer shareId);

    // 공유 토큰으로 조회 (공유 링크 접근용)
    Optional<ShareEntity> findByShareToken(String shareToken);

    // 추가: 특정 약속의 공유 개수
    long countByPromEntity(PromEntity promEntity);


}
