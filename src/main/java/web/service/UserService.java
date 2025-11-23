package web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import web.model.dto.user.AtenDto;
import web.model.dto.user.FrenDto;
import web.model.dto.user.SetDto;
import web.model.dto.user.UsersDto;
import web.model.entity.common.LangType;
import web.model.entity.common.TrafficType;
import web.model.entity.user.AtenEntity;
import web.model.entity.user.FrenEntity;
import web.model.entity.user.SetEntity;
import web.model.entity.user.UsersEntity;
import web.repository.user.AtenRepository;
import web.repository.user.FrenRepository;
import web.repository.user.SetRepository;
import web.repository.user.UsersRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    // ============================================
    // [*] DI (Dependency Injection)
    // ============================================
    // final: 불변성 보장, RequiredArgsConstructor로 자동 주입
    private final UsersRepository usersRepository;
    private final FrenRepository frenRepository;
    private final AtenRepository atenRepository;
    private final SetRepository setRepository;

    // ============================================
    // 회원 관련 메서드 (US-01 ~ US-10)
    // ============================================

    /**
     * US-01 회원가입
     *
     * @param usersDto 회원가입 정보가 담긴 DTO
     * @return 가입된 사용자 정보 DTO (실패시 원본 DTO 반환)
     */
    public UsersDto sign_up(UsersDto usersDto) {
        // 1. DTO를 Entity로 변환
        UsersEntity entity = usersDto.toEntity();

        // 2. DB 저장 (INSERT 쿼리 실행)
        // save(): JPA가 제공하는 기본 메서드, 저장 후 영속성 컨텍스트에 관리됨
        UsersEntity usersEntity = usersRepository.save(entity);

        // 3. 저장 성공 여부 확인 (ID 생성 되었는지)
        if (usersEntity.getUser_id() >= 0) {

            // 4. 성공 시 Entity를 DTO로 변환하여 반환
            return usersEntity.toDto();
        }
        // 5. 실패 시 원본 DTO 반환
        return usersDto;
    }

    /**
     * US-02 로그인
     *
     * @param usersDto 이메일과 비밀번호가 담긴 DTO
     * @return 로그인 성공시 사용자 정보 DTO, 실패시 null
     */
    public UsersDto login(UsersDto usersDto) {
        // 1. 이메일로 사용자 조회
        // Optional : 값이 있을 수도, 없을 수도 있는 컨테이너
        Optional<UsersEntity> optional = usersRepository.findByEmail(usersDto.getEmail());

        // 2. 사용자 존재하는지 확인
        if (optional.isPresent()) {
            UsersEntity usersEntity = optional.get();

            // 3. 비밀번호 확인 todo : 비크립트 등으로 암호화된 비밀번호를 비교해야함
            if (usersEntity.getPassword().equals(usersDto.getPassword())) {

                // 4. 로그인 성공 : Entity를 DTO로 변환하여 반환
                return usersEntity.toDto();
            }

            // 5. 로그인 실패
            return usersEntity.toDto();
        }
        return null;
    }

    /**
     * US-03 로그아웃
     * 세션 처리는 Controller나 Security에서 담당
     * Service에서는 특별한 로직 불필요
     */

    public boolean logout(int userId) {
        // 로그아웃 시간 기록이 필요하다면 여기에 추가하기
        // 예: 마지막 로그아웃 시간을 UsersEntity에 기록
        return true;
    }

    /**
     * US-04 이메일 중복검사
     *
     * @param email 검사할 이메일
     * @return true: 중복(사용불가), false: 사용가능
     */

    public boolean checkEmail(String email) {
        // existBy: 해당 조건의 데이터가 존재하면 true 반환
        return usersRepository.existsByEmail(email);
    }

    /**
     * US-05 연락처 중복검사
     *
     * @param phone 검사할 연락처
     * @return true: 중복(사용불가), false: 사용가능
     */
    public boolean checkPhone(String phone) {
        return usersRepository.existsByPhone(phone);
    }

    /**
     * US-06 이메일 찾기
     *
     * @param userName 사용자 이름
     * @param phone    연락처
     * @return 찾은 이메일 (없으면 null)
     */

    public String findEmail(String userName, String phone) {
        // 1. 이름과 연락처로 사용자 조회
        Optional<UsersEntity> optional = usersRepository.findByUserNameAndPhone(userName, phone);

        // 2. 사용자가 존재하면 이메일 반환
        return optional.map(UsersEntity::getEmail).orElse(null);
        // map: Optional 안의 값을 변환
        // orElse: 값이 없으면 기본값 반환
    }

    /**
     * US-07 비밀번호 찾기 (임시 비밀번호 발급)
     *
     * @param email 이메일
     * @param phone 연락처
     * @return 임시 비밀번호 (실패시 null)
     */

    public String findPassword(String email, String phone) {
        // 1. 이메일로 사용자 조회
        Optional<UsersEntity> optional = usersRepository.findByEmail(email);
        // 2. 이메일 존재 여부 확인
        if (optional.isPresent()) {
            UsersEntity usersEntity = optional.get();

            // 3. 연락처 일치 확인
            if (usersEntity.getPhone().equals(phone)) {
                // 4. 임시 비밀번호 생성
                String tempPassword = "Temp" + System.currentTimeMillis();

                // 5. 임시 비밀번호로 변경
                usersEntity.setPassword(tempPassword);
                usersRepository.save(usersEntity);

                // 6. 임시 비밀번호 반환 todo 이메일로 발송
                return tempPassword;
            }

        }
        return null;
    }


    /**
     * US-08 내 정보 수정
     *
     * @param usersDto 수정할 정보가 담긴 DTO
     * @return 수정된 사용자 정보 DTO
     */

    public UsersDto updateMyInfo(UsersDto usersDto) {
        // 1. 기존 사용자 조회
        Optional<UsersEntity> optional = usersRepository.findById(usersDto.getUser_id());

        // 존재 여부 확인
        if (optional.isPresent()) {
            UsersEntity usersEntity = optional.get();

            // 2. 수정 가능한 필드만 업데이트
            // 이메일은 변경할 수 없으니 제외
            if (usersDto.getPassword() != null) {
                usersEntity.setPassword(usersDto.getPassword());
            }
            if (usersDto.getUser_name() != null) {
                usersEntity.setUser_name(usersEntity.getUser_name());
            }
            if (usersDto.getPhone() != null) {
                usersEntity.setPhone(usersDto.getPhone());
            }
            if (usersDto.getAddr() != null) {
                usersEntity.setAddr(usersEntity.getAddr());
            }
            if (usersDto.getAddr_detail() != null) {
                usersEntity.setAddr_detail(usersEntity.getAddr_detail());
            }

            // 3. 저장 (update 쿼리 실행)
            // @Transactional 있으면 save() 호출 없이도 자동 업데이트됨 (Dirty Checking)
            UsersEntity updateEntity = usersRepository.save(usersEntity);

            // 4. DTO로 변환하여 반환
            return updateEntity.toDto();
        }

        return null;
    }

    /**
     * US-09 내 정보 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 정보 DTO
     */

    public UsersDto getMyInfo(int userId) {
        // 1. ID로 사용자 조회
        Optional<UsersEntity> optional = usersRepository.findById(userId);

        // 2. Entity를 DTO로 변환하여 반환
        return optional.map(UsersEntity::toDto).orElse(null);
    }

    /**
     * US-10 회원 탈퇴 (논리적 삭제)
     *
     * @param userId 사용자 ID
     * @return 탈퇴 성공 여부
     */

    public boolean deleteUserState(int userId) {
        // 1. 사용자 조회
        Optional<UsersEntity> optional = usersRepository.findById(userId);

        if (optional.isPresent()) {
            UsersEntity usersEntity = optional.get();

            // 2. 상태를 -1(삭제)로 변경 (논리적 삭제)
            // 물리적 삭제보다 안전: 데이터 복구 가능
            usersEntity.setUser_state(-1);
            usersRepository.save(usersEntity);

            return true;
        }
        return false;
    }

    // ============================================
    // 출석 관련 메서드 (AT-01 ~ AT-02)
    // ============================================

    /**
     * AT-01 출석하기
     *
     * @param userId 출석할 사용자 ID
     * @return 출석 정보 DTO (중복 출석시 null)
     */

    public AtenDto aten(int userId) {
        // 1. 사용자 조회
        Optional<UsersEntity> userOptional = usersRepository.findById(userId);

        if (userOptional.isPresent()) {
            UsersEntity usersEntity = userOptional.get();
            LocalDate today = LocalDate.now();

            // 2. 오늘 이미 출석했는지 확인
            Optional<AtenEntity> existingAten =
                    atenRepository.findByUsersEntityAndAtenDate(usersEntity, today);

            if (existingAten.isPresent()) {
                // 3. 이미 출석했으면 null 반환
                return null;
            }

            // 4. 새로운 출석 기록 생성
            AtenEntity atenEntity = AtenEntity.builder()
                    .aten_date(today)
                    .usersEntity(usersEntity)
                    .build();

            // 5. 저장
            AtenEntity savedEntity = atenRepository.save(atenEntity);

            // 6. DTO로 변환 후 반환
            return savedEntity.toDto();

        }

        return null;
    }

    /**
     * AT-02 출석 조회 (전체 조회)
     *
     * @param userId 사용자 ID
     * @return 출석 기록 DTO 리스트
     */

    public List<AtenDto> getAten(int userId) {
        // 1. 사용자 조회
        Optional<UsersEntity> userOptional = usersRepository.findById(userId);

        if (userOptional.isPresent()) {
            UsersEntity usersEntity = userOptional.get();

            // 2. 해당 사용자의 모든 출석 기록 조회
            List<AtenEntity> atenList = atenRepository.findByUsersEntity(usersEntity);

            // 3. Entity 리스트를 DTO 리스트로 변환
            // Stream API 사용: 함수형 프로그래밍 방식
            return atenList.stream()
                    .map(AtenEntity::toDto) // 각 Entity를 DTO로 변환
                    .collect(Collectors.toList()); // List로 반환
        }
        return new ArrayList<>(); // 빈 배열 반환
    }

    // ============================================
    // 친구 관련 메서드 (FR-01 ~ FR-06)
    // ============================================

    /**
     * FR-01 친구 요청
     *
     * @param offerUserId    요청하는 사용자 ID
     * @param receiverUserId 요청받는 사용자 ID
     * @return 친구 요청 정보 DTO (실패시 null)
     */

    public FrenDto offerFren(int offerUserId, int receiverUserId) {
        // 1. 자기 자신에게는 친구 요청 불가
        if (offerUserId == receiverUserId) {
            return null;
        }

        // 2. 두 사용자 조회
        Optional<UsersEntity> offerUserOpt = usersRepository.findById(offerUserId);
        Optional<UsersEntity> receiverUserOpt = usersRepository.findById(receiverUserId);

        if (offerUserOpt.isPresent() && receiverUserOpt.isPresent()) {
            UsersEntity offerUser = offerUserOpt.get();
            UsersEntity receiverUser = receiverUserOpt.get();

            // 3. 이미 친구 관계가 존재하는지 확인 (양방향 확인)
            Optional<FrenEntity> existing = frenRepository.findFriendship(offerUser, receiverUser);

            if (existing.isPresent()) {
                // 이미 관계가 존재하면 null 반환
                return null;
            }

            // 4. 새로운 친구 요청 생성 (초기 상태: 0 = 대기중)
            FrenEntity frenEntity = FrenEntity.builder()
                    .fren_state(0)  // 0: 친구 신청 중
                    .offerUser(offerUser)
                    .receiverUser(receiverUser)
                    .build();

            // 5. 저장
            FrenEntity savedEntity = frenRepository.save(frenEntity);

            // 6. DTO로 변환하여 반환
            return savedEntity.toDto();
        }

        return null;
    }

    /**
     * FR-02 친구 수락
     *
     * @param frenId         친구 관계 ID
     * @param receiverUserId 수락하는 사용자 ID (보안 확인용)
     * @return 수락된 친구 정보 DTO
     */

    public FrenDto receiveFren(int frenId, int receiverUserId) {
        // 1. 사용자 조회
        Optional<UsersEntity> receiverUserOpt = usersRepository.findById(receiverUserId);

        if (receiverUserOpt.isPresent()) {
            UsersEntity receiverUser = receiverUserOpt.get();

            // 2. 친구 요청 조회 (나에게 온 요청인지 확인)
            Optional<FrenEntity> frenOpt =
                    frenRepository.findByFrenIdAndReceiverUser(frenId, receiverUser);

            if (frenOpt.isPresent()) {
                FrenEntity frenEntity = frenOpt.get();

                // 3. 상태를 1(친구)로 변경
                frenEntity.setFren_state(1);

                // 4. 저장 (UPDATE)
                FrenEntity updatedEntity = frenRepository.save(frenEntity);

                return updatedEntity.toDto();
            }
        }

        return null;
    }

    /**
     * FR-03 친구 거절
     *
     * @param frenId         친구 관계 ID
     * @param receiverUserId 거절하는 사용자 ID
     * @return 거절 성공 여부
     */

    public boolean negativeFren(int frenId, int receiverUserId) {
        // 1. 사용자 조회
        Optional<UsersEntity> receiverUserOpt = usersRepository.findById(receiverUserId);

        if (receiverUserOpt.isPresent()) {
            UsersEntity receiverUser = receiverUserOpt.get();

            // 2. 친구 요청 조회
            Optional<FrenEntity> frenOpt =
                    frenRepository.findByFrenIdAndReceiverUser(frenId, receiverUser);

            if (frenOpt.isPresent()) {
                // 3. 삭제 (거절은 관계 자체를 삭제)
                frenRepository.deleteById(frenId);
                return true;
            }
        }
        return false;
    }

    /**
     * FR-04 친구 전체조회
     *
     * @param userId 사용자 ID
     * @return 친구 목록 DTO 리스트 (상태가 1인 친구만)
     */

    public List<FrenDto> getFren(int userId) {
        // 1. 사용자 조회
        Optional<UsersEntity> userOpt = usersRepository.findById(userId);

        if (userOpt.isPresent()) {
            UsersEntity user = userOpt.get();

            // 2. 상태가 1(현재 친구)인 모든 친구 관계 조회
            List<FrenEntity> frenList =
                    frenRepository.findAllFriendByUserAndState(user, 1);

            // 3. Entity 리스트를 DTO 리스트로 변환
            return frenList.stream()
                    .map(FrenEntity::toDto)
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    /**
     * FR-05 친구 상세조회
     *
     * @param frenId 친구 관계 ID
     * @return 친구 상세 정보 DTO
     */

    public FrenDto getDetailFren(int frenId) {
        // 1. 친구 관계 조회
        Optional<FrenEntity> frenOpt = frenRepository.findById(frenId);

        // 2. DTO로 변환 하여 반환
        return frenOpt.map(FrenEntity::toDto).orElse(null);
    }

    /**
     * FR-06 친구 삭제 (논리적 삭제)
     *
     * @param frenId 친구 관계 ID
     * @param userId 삭제 요청한 사용자 ID (권한 확인용)
     * @return 삭제 성공 여부
     */

    public boolean deleteFren(int frenId, int userId) {
        // 1. 친구 관계 조회
        Optional<FrenEntity> frenOpt = frenRepository.findById(frenId);

        if (frenOpt.isPresent()) {
            FrenEntity frenEntity = frenOpt.get();

            // 2. 권한 확인: 요청자가 해당 친구 관계 당사자인지
            boolean isAuthorized =
                    frenEntity.getOfferUser().getUser_id() == userId ||
                            frenEntity.getReceiverUser().getUser_id() == userId;
            if (isAuthorized) {
                // 3. 상태를 -1(삭제)로 변경 (논리적 삭제)
                frenEntity.setFren_state(-1);
                frenRepository.save(frenEntity);

                return true;
            }
        }
        return false;
    }

    // ============================================
    // 설정 관련 메서드 (ST-01 ~ ST-05)
    // ============================================

    /**
     * 설정 업데이트 공통 로직 - 헬퍼 메소드
     * @param userId 사용자 ID
     * @param updater 업데이트 함수 (람다)
     * @return 업데이트 성공 여부
     */

    private boolean updateSetting(int userId, java.util.function.Consumer<SetEntity> updater) {
        // 1. 사용자 조회
        Optional<UsersEntity> userOpt = usersRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            UsersEntity user = userOpt.get();
            
            // 2. 설정 조회 또는 생성
            SetEntity setEntity = setRepository.findByUsersEntity(user)
                    .orElseGet(() -> SetEntity.builder()
                            .usersEntity(user)
                            .build());

            // 3. 업데이트 적용 (람다 함수 실행)
            updater.accept(setEntity);

            // 4. 저장
            setRepository.save(setEntity);
            return true;
        }

        return false;
    }


    /**
     * ST-01 약속 리마인드 설정
     * @param userId 사용자 ID
     * @param remindMinutes 알림 시간 (분 단위)
     * @return 설정 성공 여부
     */

    public boolean setRemind(int userId, int remindMinutes) {
        return updateSetting(userId, entity -> entity.setSet_remind(remindMinutes));
    }

    /**
     * ST-02 업무표시 설정
     * @param userId 사용자 ID
     * @param workDisplay 업무 표시 여부 (0: 비활성, 1: 활성)
     * @return 설정 성공 여부
     */

    public boolean setWork(int userId, int workDisplay) {
        return updateSetting(userId, entity -> entity.setSet_work(workDisplay));
    }

    /**
     * ST-03 우선교통수단 설정
     * @param userId 사용자 ID
     * @param trafficType 교통수단 타입
     * @return 설정 성공 여부
     */

    public boolean setTraffic(int userId, TrafficType trafficType) {
        return updateSetting(userId, entity -> entity.setSet_traffic(trafficType));
    }

    /**
     * ST-04 언어 설정
     * @param userId 사용자 ID
     * @param langType 언어 타입
     * @return 설정 성공 여부
     */

    public boolean setLang(int userId, LangType langType) {
        return updateSetting(userId, entity -> entity.setSet_language(langType));
    }

    /**
     * ST-05 설정 초기화
     * @param userId 사용자 ID
     * @return 초기화 성공 여부
     */

    public boolean setReset(int userId) {
        // 1. 사용자 조회
        Optional<UsersEntity> userOpt = usersRepository.findById(userId);

        if (userOpt.isPresent()) {
            UsersEntity user = userOpt.get();

            // 2. 기존 설정 조회
            Optional<SetEntity> setOpt = setRepository.findByUsersEntity(user);

            if (setOpt.isPresent()) {
                SetEntity setEntity = setOpt.get();

                // 3. 기본값으로 촉기화
                setEntity.setSet_remind(0);
                setEntity.setSet_work(1);
                setEntity.setSet_traffic(TrafficType.SUBWAY_AND_BUS);
                setEntity.setSet_language(LangType.KOREAN);

                // 4. 저장
                setRepository.save(setEntity);
                return true;
            }

        }
        return false;
    }

    /**
     * 설정 조회 (추가 메서드)
     * @param userId 사용자 ID
     * @return 설정 정보 DTO
     */

    public SetDto getSetting(int userId) {
        // 1. 사용자 조회
        Optional<UsersEntity> userOpt = usersRepository.findById(userId);

        if (userOpt.isPresent()) {
            UsersEntity user = userOpt.get();

            // 2. 설정 조회
            Optional<SetEntity> setOpt = setRepository.findByUsersEntity(user);

            if (setOpt.isPresent()) {
                return setOpt.get().toDto();
            } else {
                // 3. 설정이 없으면 기본 설정 생성
                SetEntity newSetting = SetEntity.builder()
                        .usersEntity(user)
                        .build();   // @Builder.Default 값들이 자동 설정됨

                SetEntity savedEntity = setRepository.save(newSetting);
                return savedEntity.toDto();
            }
        }

        return null;
    }



    // AU-01	관리자 권한 부여/박탈(관)	update_roll()

}
