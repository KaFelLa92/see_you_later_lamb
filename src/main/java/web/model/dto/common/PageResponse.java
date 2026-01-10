package web.model.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 페이징 처리된 응답 DTO
 * Spring Data JPA의 Page 객체를 프론트엔드 친화적인 형태로 변환
 *
 * 사용 예시:
 * Page<LambEntity> page = lambRepository.findAll(pageable);
 * PageResponse<LambDto> response = PageResponse.of(
 *     page.getContent().stream().map(LambEntity::toDto).collect(Collectors.toList()),
 *     page
 * );
 *
 * @param <T> 리스트 아이템의 타입
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    /**
     * 실제 데이터 리스트
     * 현재 페이지에 표시될 항목들
     */
    private List<T> content;

    /**
     * 현재 페이지 번호
     * 0부터 시작 (0 = 첫 페이지)
     */
    private int currentPage;

    /**
     * 페이지당 항목 수
     * 한 페이지에 표시할 최대 항목 개수
     */
    private int pageSize;

    /**
     * 전체 항목 수
     * 모든 페이지의 총 데이터 개수
     */
    private long totalElements;

    /**
     * 전체 페이지 수
     * (전체 항목 수 ÷ 페이지당 항목 수)의 올림 값
     */
    private int totalPages;

    /**
     * 첫 페이지 여부
     * 현재 페이지가 첫 번째 페이지인지
     */
    private boolean first;

    /**
     * 마지막 페이지 여부
     * 현재 페이지가 마지막 페이지인지
     */
    private boolean last;

    /**
     * 비어있는 페이지 여부
     * 데이터가 하나도 없는지
     */
    private boolean empty;

    // ========== 정적 팩토리 메서드 ==========

    /**
     * Spring Data JPA의 Page 객체를 PageResponse로 변환
     *
     * @param content 변환된 DTO 리스트
     * @param page Spring Data JPA Page 객체
     * @param <T> DTO 타입
     * @return PageResponse 페이징 응답 객체
     */
    public static <T> PageResponse<T> of(List<T> content, Page<?> page) {
        return PageResponse.<T>builder()
                .content(content)
                .currentPage(page.getNumber())           // 현재 페이지 번호
                .pageSize(page.getSize())                // 페이지 크기
                .totalElements(page.getTotalElements())  // 전체 항목 수
                .totalPages(page.getTotalPages())        // 전체 페이지 수
                .first(page.isFirst())                   // 첫 페이지 여부
                .last(page.isLast())                     // 마지막 페이지 여부
                .empty(page.isEmpty())                   // 비어있는지 여부
                .build();
    }

    /**
     * Page 객체를 직접 PageResponse로 변환
     * (Entity를 DTO로 변환하지 않고 그대로 사용하는 경우)
     *
     * @param page Spring Data JPA Page 객체
     * @param <T> 항목 타입
     * @return PageResponse 페이징 응답 객체
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}