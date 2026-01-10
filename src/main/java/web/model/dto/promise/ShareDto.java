package web.model.dto.promise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.promise.PromEntity;
import web.model.entity.promise.ShareEntity;
import web.model.entity.promise.TempEntity;
import web.model.entity.user.UsersEntity;

/**
 * 공유(Share) DTO
 * 약속 공유 정보를 전달하기 위한 데이터 전송 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareDto {

    // ========== 1. 필드 설계 ==========

    private int shareId;                   // 약속공유번호 (PK)
    private String shareToken;             // 공유 토큰 (URL에 사용)

    /**
     * 약속 확인 상태
     * -1: 약속 어김
     * 0: 약속 이행
     * 1: 약속 잘 지킴
     */
    private int shareCheck;                // 약속 확인

    /**
     * 약속 점수
     * 1~5점, 5점일수록 좋음
     */
    private int shareScore;                // 약속 점수

    private String shareFeedback;          // 약속 피드백
    private int promId;                    // 약속번호 (FK)
    private String createDate;             // 생성일
    private String updateDate;             // 수정일

    // ========== 2. Dto -> Entity 변환 메서드 ==========
    /**
     * DTO를 엔티티로 변환하는 메서드 (생성용)
     * @param promEntity 약속 엔티티
     * @param usersEntity 사용자 엔티티
     * @return ShareEntity 공유 엔티티
     */
    public ShareEntity toEntity(PromEntity promEntity, UsersEntity usersEntity) {
        return ShareEntity.builder()
                .shareId(shareId)
                .shareToken(shareToken)
                .shareCheck(shareCheck)
                .shareScore(shareScore)
                .shareFeedback(shareFeedback)
                .promEntity(promEntity)
                .build();
    }
}