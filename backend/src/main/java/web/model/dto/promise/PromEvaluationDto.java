package web.model.dto.promise;

// ============================================
// 평가용 DTO 추가
// ============================================

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 약속 평가용 DTO
 * - 평가자 정보 + 평가 내용을 함께 전달
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromEvaluationDto {

    // 평가자 정보
    private Integer user_id;                // 사용자 ID (회원인 경우)
    private Integer temp_id;                // 임시 사용자 ID (비회원인 경우)
    private String temp_name;               // 임시 사용자 이름 (비회원인 경우)

    // 평가 내용 (Share 테이블에 저장될 정보)
    private int share_check;                // 약속확인 -1 : 약속 어김 , 0 : 약속 이행 , 1 : 약속 잘 지킴
    private int share_score;                // 약속점수  1~5점. 5점일수록 좋음
    private String share_feedback;          // 약속피드백
}
