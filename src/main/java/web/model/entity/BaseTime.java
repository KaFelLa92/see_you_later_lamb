package web.model.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 엔티티 상속
@EntityListeners(AuditingEntityListener.class) // 해당 엔티티 자동으로 감시 적용
public class BaseTime  {

    @CreatedDate
    private LocalDateTime create_date;

    @LastModifiedBy
    private LocalDateTime update_date;
}
