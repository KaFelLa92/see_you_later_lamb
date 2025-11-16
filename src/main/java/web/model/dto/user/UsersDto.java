package web.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.model.entity.common.UserRole;
import web.model.entity.user.UsersEntity;

@Data @Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersDto {

    // 1. 테이블 설계
    private int user_id;        // 사용자번호 (PK)
    private String email;       // 이메일
    private String password;    // 비밀번호
    private String user_name;   // 양치기이름
    private String phone;       // 연락처
    private String addr;        // 도로명주소
    private String addr_detail; // 상세주소
    private int user_state;     // 사용자상태
    private int signup_type;    // 가입방법
    private String create_date; // 생성일
    private String update_date; // 수정일
    private UserRole role;      // 권한

    // 2. Dto -> Entity 변환 : C
    public UsersEntity toEntity() {
        return UsersEntity.builder()
                .user_id(user_id)
                .email(email)
                .password(password)
                .user_name(user_name)
                .phone(phone)
                .addr(addr)
                .addr_detail(addr_detail)
                .user_state(user_state)
                .signup_type(signup_type)
                .role( role )
                .build();
    }

}
