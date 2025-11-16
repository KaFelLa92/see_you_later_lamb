package web.model.dto.promise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.promise.PromEntity;
import web.model.entity.user.UsersEntity;

import java.time.LocalDateTime;

@Data @Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromDto {

    // 1. 테이블 설계
    private int prom_id;                    // 약속번호 (PK)
    private String prom_title;              // 약속명
    private LocalDateTime prom_date;        // 약속일시
    private int prom_alert;                 // 약속알림시간 , 약속시간보다 이전에 알림을 하며, 분 단위로 측정한다. 0이면 알람 없음.
    private String prom_addr;               // 약속주소(장소)
    private String prom_addr_detail;        // 약속상세장소
    private Double prom_lat;                // 약속장소위도
    private Double prom_lng;                // 약속장소경도
    private String prom_text;               // 약속내용
    private String prom_memo;               // 약속메모/비고
    private int user_id;                    // 사용자번호 (FK)
    private String create_date;             // 생성일
    private String update_date;             // 수정일

    // 2. Dto -> Entity 변환 : C
    public PromEntity toEntity(UsersEntity usersEntity) {
        return PromEntity.builder()
                .prom_id(prom_id)
                .prom_title(prom_title)
                .prom_date(prom_date)
                .prom_alert(prom_alert)
                .prom_addr(prom_addr)
                .prom_addr_detail(prom_addr_detail)
                .prom_lat(prom_lat)
                .prom_lng(prom_lng)
                .prom_text(prom_text)
                .prom_memo(prom_memo)
                .usersEntity(usersEntity)
                .build();
    }

}
