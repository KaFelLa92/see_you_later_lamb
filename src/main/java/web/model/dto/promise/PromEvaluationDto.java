package web.model.dto.promise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 약속 평가용 DTO
 * 평가자 정보 + 평가 내용을 함께 전달하기 위한 특수 목적 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromEvaluationDto {

    // ========== 평가자 정보 ==========

    private Integer userId;                // 사용자 ID (회원인 경우)
    private Integer tempId;                // 임시 사용자 ID (비회원인 경우)
    private String tempName;               // 임시 사용자 이름 (비회원인 경우)

    // ========== 평가 내용 (Share 테이블에 저장될 정보) ==========

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
}