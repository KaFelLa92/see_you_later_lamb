package web.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.user.FrenEntity;
import web.model.entity.user.UsersEntity;

/**
 * 친구(Friend) DTO
 * 친구 관계 정보를 전달하기 위한 데이터 전송 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FrenDto {

    // ========== 1. 필드 설계 ==========

    private int frenId;        // 친구번호 (PK)

    /**
     * 친구 상태 필드
     * 1: 현재 친구 (수락됨)
     * 0: 친구 신청 중 (대기)
     * -1: 더 이상 친구 아님 (삭제)
     */
    private int frenState;     // 친구 상태

    private int frenOffer;     // 친구요청자 (FK/user_id)
    private int frenReceiver;  // 친구수락자 (FK/user_id)
    private String createDate;     // 생성일
    private String updateDate;     // 수정일

    // ========== 2. Dto -> Entity 변환 메서드 ==========
    /**
     * DTO를 엔티티로 변환하는 메서드 (생성용)
     * @param offerUser 친구 요청을 보낸 사용자 엔티티
     * @param receiverUser 친구 요청을 받은 사용자 엔티티
     * @return FrenEntity 친구 관계 엔티티
     */
    public FrenEntity toEntity(UsersEntity offerUser, UsersEntity receiverUser) {
        return FrenEntity.builder()
                .frenId(frenId)
                .frenState(frenState)
                .offerUser(offerUser)
                .receiverUser(receiverUser)
                .build();
    }
}