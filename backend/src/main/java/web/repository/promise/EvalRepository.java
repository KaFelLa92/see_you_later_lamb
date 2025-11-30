package web.repository.promise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import web.model.entity.promise.EvalEntity;
import web.model.entity.promise.ShareEntity;
import web.model.entity.user.UsersEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvalRepository extends JpaRepository<EvalEntity, Integer> {

    //  PM-07	약속 공유	share_prom()

    // PM-08 약속 평가 - 특정 공유에 대한 모든 평가 조회
    List<EvalEntity> findByShareEntity(ShareEntity shareEntity);

    // PM-08 약속 평가 - 특정 사용자가 특정 공유를 평가했는지 확인 (중복 방지)
    Optional<EvalEntity> findByShareEntityAndUsersEntity(
            ShareEntity shareEntity,
            UsersEntity usersEntity
    );

    // PM-08 약속 평가 - 임시 사용자가 평가했는지 확인
    @Query("SELECT e FROM EvalEntity e WHERE e.shareEntity = :share AND e.tempEntity.tempId = :tempId")
    Optional<EvalEntity> findByShareAndTempId(
            @Param("share") ShareEntity shareEntity,
            @Param("tempId") int tempId
    );

    // 추가: 특정 공유에 평가가 이미 존재하는지 확인 (1개만 허용)
    boolean existsByShareEntity(ShareEntity shareEntity);

    // 추가: 사용자가 작성한 모든 평가 조회
    List<EvalEntity> findByUsersEntity(UsersEntity usersEntity);
}
