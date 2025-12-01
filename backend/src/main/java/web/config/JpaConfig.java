package web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 설정 클래스
 *
 * @EnableJpaAuditing:
 * - JPA Auditing 기능 활성화
 * - BaseTime의 @CreatedDate, @LastModifiedDate가 동작하도록 함
 * - 엔티티가 생성/수정될 때 자동으로 시간 저장
 *
 * 동작 원리:
 * 1. @EntityListeners(AuditingEntityListener.class)가 적용된 엔티티 감지
 * 2. 엔티티 저장 시점에 @CreatedDate 필드에 현재 시간 자동 설정
 * 3. 엔티티 수정 시점에 @LastModifiedDate 필드에 현재 시간 자동 설정
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // JPA Auditing 활성화만 하면 되므로 별도 설정 없음
}