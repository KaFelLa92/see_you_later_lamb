package web.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.user.FrenEntity;
import web.model.entity.user.UsersEntity;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class FrenDto {

    // 1. 테이블 설계
    private int fren_id;        // 친구번호 (PK)
    private int fren_state;     // 친구 상태 1 : 현재 친구 , 0 : 친구 신청 중 , -1 : 더 이상 친구 아님(삭제)
    private int fren_offer;     // 친구요청자 (FK/user_id)
    private int fren_receiver;  // 친구수락자 (FK/user_id)
    private String create_date;     // 생성일
    private String update_date;     // 수정일

    // 2. Dto -> Entity 변환 : C
    public FrenEntity toEntity(UsersEntity offerUser , UsersEntity receiverUser) {
        return FrenEntity.builder()
                .fren_id( fren_id )
                .fren_state( fren_state )
                .offerUser( offerUser )
                .receiverUser( receiverUser )
                .build();
    }
}
