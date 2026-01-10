package web.model.dto.promise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.promise.PromEntity;
import web.model.entity.user.UsersEntity;

import java.time.LocalDateTime;

/**
 * 약속(Promise) DTO
 * 약속 정보를 전달하기 위한 데이터 전송 객체
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromDto {

    // ========== 1. 필드 설계 ==========

    private int promId;                    // 약속번호 (PK)
    private String promTitle;              // 약속명
    private LocalDateTime promDate;        // 약속일시

    /**
     * 약속 알림 시간
     * - 약속 시간보다 이전에 알림, 분 단위
     * - 0이면 알람 없음
     */
    private int promAlert;                 // 약속 알림 시간

    private String promAddr;               // 약속 주소(장소)
    private String promAddrDetail;        // 약속 상세 장소
    private Double promLat;                // 약속 장소 위도
    private Double promLng;                // 약속 장소 경도
    private String promText;               // 약속 내용
    private String promMemo;               // 약속 메모/비고
    private int userId;                    // 사용자번호 (FK)
    private String createDate;             // 생성일
    private String updateDate;             // 수정일

    // ========== 2. Dto -> Entity 변환 메서드 ==========
    /**
     * DTO를 엔티티로 변환하는 메서드 (생성용)
     * @param usersEntity 사용자 엔티티
     * @return PromEntity 약속 엔티티
     */
    public PromEntity toEntity(UsersEntity usersEntity) {
        return PromEntity.builder()
                .promId(promId)
                .promTitle(promTitle)
                .promDate(promDate)
                .promAlert(promAlert)
                .promAddr(promAddr)
                .promAddrDetail(promAddrDetail)
                .promLat(promLat)
                .promLng(promLng)
                .promText(promText)
                .promMemo(promMemo)
                .usersEntity(usersEntity)
                .build();
    }
}