package web.repository.user;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import web.model.entity.user.FrenEntity;
import web.model.entity.user.UsersEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface FrenRepository extends JpaRepository<FrenEntity , Integer> {

    //  FR-01	친구 요청	offer_fren() : 친구 요청의 존재 여부 확인
    // 양방향 확인 : A > B 또는 B > A 중 하나라도 존재하는지
    @Query("select f from FrenEntity f where " +
            " (f.offerUser = :user1 and f.receiverUser = :user2) or " +
            " (f.offerUser = :user2 and f.receiverUser = :user1) ")
    Optional<FrenEntity> findFriendship(@Param("user1")UsersEntity user1,
                                        @Param("user2")UsersEntity user2);

    //  FR-02	친구 수락	receive_fren() 나에게 온 친구 요청 찾기
    Optional<FrenEntity> findByFrenIdAndReceiverUser(Integer frenId, UsersEntity receiverUser);

    //  FR-03	친구 거절	nagative_fren()

    //  FR-04	친구 전체조회	get_fren()
    //  내가 요청한 친구 목록 (상태가 1인 친구만)
    List<FrenEntity> findByOfferUserAndFrenState(UsersEntity offerUser, int frenState);
    //  내가 받은 친구 목록 (상태가 1인 친구만)
    List<FrenEntity> findByReceiverUserAndFrenState(UsersEntity receiverUser, int frenState);

    //  FR-05	친구 상세조회	get_detail_fren()
    // 친구 관계 ID로 조회
    Optional<FrenEntity> findById(Integer frenId);

    //  FR-06	친구 삭제	delete_fren()

    // 추가 : 친구 관계에서 특정 사용자가 포함된 모든 관계 조회
    @Query("select f from FrenEntity f where " +
            " (f.offerUser = :user or f.receiverUser = :user) and f.frenState = :state")
    List<FrenEntity> findAllFriendByUserAndState(@Param("user") UsersEntity user,
                                                 @Param("state") int state);

}
