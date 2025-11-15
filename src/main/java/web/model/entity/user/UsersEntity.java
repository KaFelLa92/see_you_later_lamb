package web.model.entity.user;

import jakarta.persistence.*;
import lombok.*;
import web.model.dto.user.UsersDto;
import web.model.entity.BaseTime;
import web.model.entity.common.UserRole;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table( name = "users" )
@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersEntity extends BaseTime {

    // 1. 테이블 설계
    @Id // PK
    @GeneratedValue( strategy = GenerationType.IDENTITY ) // auto_increment
    private int user_id;                                                    // 사용자번호
    @Column( nullable = false , length = 40 )
    private String email;                                                   // 이메일
    @Column( nullable = false , length = 40 )
    private String password;                                                // 비밀번호
    @Column( nullable = false , length = 30 )
    private String user_name;                                               // 양치기이름
    @Column( nullable = false , length = 15 )
    private String phone;                                                   // 연락처
    @Column( nullable = false )
    private String addr;                                                    // 도로명주소
    @Column( nullable = false )
    private String addr_detail;                                             // 상세주소
    @Builder.Default                                                        // -1 : 삭제계정 , 0 : 휴면계정 , 1 : 활동계정
    private int user_state = 1;                                             // 사용자상태
    @Builder.Default                                                        // 1: 일반 , 2 : 구글 , 3 : 카카오 , 4 : 네이버
    private int signup_type = 1;                                            // 가입방법
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.ROLE_USER;                          // 사용자권한

    // 2. 양방향연결
    // 상위 엔티티가 하위 엔티티 참조관계
    // 양방향 연결: 내가 요청한 친구 관계
    @OneToMany(mappedBy = "offerUser", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<FrenEntity> sentFriendRequests = new ArrayList<>();

    // 양방향 연결: 내가 받은 친구 관계
    @OneToMany(mappedBy = "receiverUser", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<FrenEntity> receivedFriendRequests = new ArrayList<>();

    // 양방향 연결: 출석
    @OneToMany(mappedBy = "usersEntity", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<AtenEntity> atenEntityList = new ArrayList<>();

    // 3. Entity -> Dto 변환 : R
    public UsersDto toDto () {
        return UsersDto.builder()
                .user_id( this.user_id )
                .email( this.email )
                .password( this.password )
                .user_name( this.user_name )
                .phone( this.phone )
                .addr( this.addr )
                .addr_detail( this.addr_detail )
                .user_state( this.user_state )
                .signup_type( this.signup_type )
                .create_date( this.getCreate_date().toString() )
                .update_date( this.getUpdate_date().toString() )
                .build();
    }

}
