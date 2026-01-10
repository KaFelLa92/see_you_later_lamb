package web.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.common.LangType;
import web.model.entity.common.TrafficType;
import web.model.entity.user.SetEntity;
import web.model.entity.user.UsersEntity;

/**
 * 설정(Setting) DTO
 * 사용자 개인 설정 정보를 전달하기 위한 데이터 전송 객체
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetDto {

    // ========== 1. 필드 설계 ==========

    private int setId;                 // 설정번호 (PK)

    /**
     * 약속 리마인드 시간
     * - 약속 시간 기준으로 '몇 분 전'에 알람을 줄 것인가
     * - 0일 경우 리마인드 설정 해제
     */
    private int setRemind;             // 약속 리마인드

    /**
     * 업무 표시 여부
     * - 시간마다 들어오는 목장 업무를 플레이할 것인가
     * 0: 플레이 안함
     * 1: 플레이함
     */
    private int setWork;               // 업무 표시

    private TrafficType setTraffic;    // 우선 교통수단
    private LangType setLanguage;      // 언어 설정
    private int userId;                // 사용자번호 (FK)
    private String createDate;         // 생성일
    private String updateDate;         // 수정일

    // ========== 2. Dto -> Entity 변환 메서드 ==========
    /**
     * DTO를 엔티티로 변환하는 메서드 (생성용)
     * @param usersEntity 사용자 엔티티
     * @return SetEntity 설정 엔티티
     */
    public SetEntity toEntity(UsersEntity usersEntity) {
        return SetEntity.builder()
                .setId(setId)
                .setRemind(setRemind)
                .setWork(setWork)
                .setTraffic(setTraffic)
                .setLanguage(setLanguage)
                .usersEntity(usersEntity)
                .build();
    }
}