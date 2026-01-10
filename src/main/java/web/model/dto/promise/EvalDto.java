package web.model.dto.promise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.promise.EvalEntity;
import web.model.entity.promise.ShareEntity;
import web.model.entity.promise.TempEntity;
import web.model.entity.user.UsersEntity;

/**
 * 평가(Evaluation) DTO
 * 약속 평가자 정보를 전달하기 위한 데이터 전송 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvalDto {

    // ========== 1. 필드 설계 ==========

    private int evalId;                // 약속평가자번호 (PK)
    private int userId;                // 사용자번호 (FK) - 회원인 경우
    private int tempId;                // 임시사용자번호 (FK) - 비회원인 경우
    private int shareId;               // 약속공유번호 (FK)
    private String createDate;         // 생성일
    private String updateDate;         // 수정일

    // ========== 2. Dto -> Entity 변환 메서드 ==========
    /**
     * DTO를 엔티티로 변환하는 메서드 (생성용)
     * @param usersEntity 사용자 엔티티 (회원)
     * @param tempEntity 임시사용자 엔티티 (비회원)
     * @param shareEntity 약속공유 엔티티
     * @return EvalEntity 평가 엔티티
     */
    public EvalEntity toEntity(UsersEntity usersEntity, TempEntity tempEntity, ShareEntity shareEntity) {
        return EvalEntity.builder()
                .evalId(evalId)
                .usersEntity(usersEntity)
                .tempEntity(tempEntity)
                .shareEntity(shareEntity)
                .build();
    }
}