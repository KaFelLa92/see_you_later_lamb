package web.model.dto.lamb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.lamb.LambEntity;
import web.model.entity.lamb.ShepEntity;
import web.model.entity.user.UsersEntity;

/**
 * 양치기(Shepherd) DTO
 * 양과 사용자를 연결하는 데이터 전송 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShepDto {

    // ========== 1. 필드 설계 ==========

    private int shepId;                    // 양치기번호 (PK)
    private String shepName;               // 양 별명 (사용자가 지어준 이름)

    /**
     * 양 배고픔 상태
     * -1: 배고픔
     * 0: 보통
     * 1: 배부름
     */
    private int shepHunger;                // 양 배고픔

    /**
     * 양털 상태
     * -1: 털 많음
     * 0: 털 보통
     * 1: 털 없음
     */
    private int shepFur;                   // 양털 상태

    /**
     * 양 존재 여부
     * 1: 울타리에 있음
     * 0: 목장에 있음
     * -1: 늑대에 쫓기는 중(사용자 소유 아님)
     */
    private int shepExist;                 // 양 존재 여부

    private int lambId;                    // 양번호 (FK)
    private int userId;                    // 사용자번호 (FK)
    private String createDate;             // 생성일
    private String updateDate;             // 수정일

    // ========== 2. Dto -> Entity 변환 메서드 ==========
    /**
     * DTO를 엔티티로 변환하는 메서드 (생성용)
     * @param lambEntity 양 정보 엔티티
     * @param usersEntity 사용자 엔티티
     * @return ShepEntity 양치기 엔티티
     */
    public ShepEntity toEntity(LambEntity lambEntity, UsersEntity usersEntity) {
        return ShepEntity.builder()
                .shepId(shepId)
                .shepName(shepName)
                .shepHunger(shepHunger)
                .shepFur(shepFur)
                .shepExist(shepExist)
                .lambEntity(lambEntity)
                .usersEntity(usersEntity)
                .build();
    }
}