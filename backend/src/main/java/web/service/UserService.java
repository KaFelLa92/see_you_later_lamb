package web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

/**
 * 사용자 관련 비즈니스 로직 처리 서비스
 * - 회원 관리 (가입, 로그인, 정보 수정 등)
 * - 출석 관리
 * - 친구 관리
 * - 설정 관리
 */
@Service
@RequiredArgsConstructor  // final 필드에 대한 생성자 자동 생성 (의존성 주입용)
@Transactional            // 클래스 레벨의 트랜잭션 처리 (모든 메서드에 적용)
public class UserService {

    // ============================================
    // [*] DI (Dependency Injection) - 의존성 주입
    // ============================================

    /**
     * final: 불변성 보장 (한 번 할당되면 변경 불가)
     * RequiredArgsConstructor가 자동으로 생성자를 만들어 주입
     */
    private final UsersRepository usersRepository;  // 사용자 Repository
    private final FrenRepository frenRepository;    // 친구 Repository
    private final AtenRepository atenRepository;    // 출석 Repository
    private final SetRepository setRepository;      // 설정 Repository

    // ============================================
    // 회원 관련 메서드 (US-01 ~ US-10)
    // ============================================

    /**
     * US-01 회원가입
     * 새로운 사용자를 데이터베이스에 등록
     *
     * @param usersDto 회원가입 정보가 담긴 DTO
     * @return UsersDto 가입된 사용자 정보 DTO (실패 시 원본 DTO 반환)
     */
    public UsersDto signUp(UsersDto usersDto) {
        // 1. DTO를 Entity로 변환
        // DTO: 데이터 전송 객체 (클라이언트 ↔ 서버)
        // Entity: 데이터베이스 테이블과 매핑되는 객체
        UsersEntity entity = usersDto.toEntity();

        // 2. DB 저장 (INSERT 쿼리 실행)
        // save(): JPA가 제공하는 기본 메서드
        // 저장 후 영속성 컨텍스트에 관리되며, ID가 자동 생성됨
        UsersEntity usersEntity = usersRepository.save(entity);

        // 3. 저장 성공 여부 확인 (ID 생성 되었는지)
        // 자동 생성된 ID가 0 이상이면 저장 성공
        if (usersEntity.getUserId() >= 0) {
            // 4. 성공 시 Entity를 DTO로 변환하여 반환
            // 클라이언트에게 전달하기 위해 DTO로 변환
            return usersEntity.toDto();
        }

        // 5. 실패 시 원본 DTO 반환
        return usersDto;
    }

    /**
     * US-02 로그인
     * 이메일과 비밀번호로 사용자 인증
     *
     * @param usersDto 이메일과 비밀번호가 담긴 DTO
     * @return UsersDto 로그인 성공 시 사용자 정보 DTO, 실패 시 null
     */
    public UsersDto login(UsersDto usersDto) {
        // 1. 이메일로 사용자 조회
        // Optional: 값이 있을 수도, 없을 수도 있는 컨테이너
        // NullPointerException을 방지하기 위한 Java 8의 기능
        Optional<UsersEntity> optional = usersRepository.findByEmail(usersDto.getEmail());

        // 2. 사용자 존재하는지 확인
        if (optional.isPresent()) {
            // Optional에서 실제 Entity 가져오기
            UsersEntity usersEntity = optional.get();

            // 3. 비밀번호 확인
            // TODO: BCryptPasswordEncoder 등으로 암호화된 비밀번호를 비교해야 함
            // 예: passwordEncoder.matches(usersDto.getPassword(), usersEntity.getPassword())
            if (usersEntity.getPassword().equals(usersDto.getPassword())) {
                // 4. 로그인 성공: Entity를 DTO로 변환하여 반환
                return usersEntity.toDto();
            }

            // 5. 비밀번호 불일치
            return null;
        }

        // 6. 사용자 없음
        return null;
    }

    /**
     * US-03 로그아웃
     * 세션 처리는 Controller나 Security에서 담당
     * Service에서는 특별한 로직 불필요 (로그 기록 등만 필요 시 추가)
     *
     * @param userId 사용자 ID
     * @return boolean 로그아웃 성공 여부
     */
    public boolean logout(int userId) {
        // 로그아웃 시간 기록이 필요하다면 여기에 추가
        // 예: 마지막 로그아웃 시간을 UsersEntity에 기록
        //
        // Optional<UsersEntity> userOpt = usersRepository.findById(userId);
        // if (userOpt.isPresent()) {
        //     UsersEntity user = userOpt.get();
        //     user.setLastLogoutTime(LocalDateTime.now());
        //     usersRepository.save(user);
        //     return true;
        // }
        // return false;

        return true;
    }

    /**
     * US-04 이메일 중복검사
     * 회원가입 시 이메일이 이미 사용 중인지 확인
     *
     * @param email 검사할 이메일
     * @return boolean true: 중복(사용불가), false: 사용가능
     */
    public boolean checkEmail(String email) {
        // existsBy: 해당 조건의 데이터가 존재하면 true 반환
        // SELECT COUNT(*) > 0 형태의 쿼리 실행
        return usersRepository.existsByEmail(email);
    }

    /**
     * US-05 연락처 중복검사
     * 회원가입 시 연락처가 이미 사용 중인지 확인
     *
     * @param phone 검사할 연락처
     * @return boolean true: 중복(사용불가), false: 사용가능
     */
    public boolean checkPhone(String phone) {
        return usersRepository.existsByPhone(phone);
    }

    /**
     * US-06 이메일 찾기
     * 이름과 연락처로 사용자의 이메일을 찾음
     *
     * @param userName 사용자 이름
     * @param phone    연락처
     * @return String 찾은 이메일 (없으면 null)
     */
    public String findEmail(String userName, String phone) {
        // 1. 이름과 연락처로 사용자 조회
        Optional<UsersEntity> optional = usersRepository.findByUserNameAndPhone(userName, phone);

        // 2. 사용자가 존재하면 이메일 반환
        // map: Optional 안의 값을 변환
        // orElse: 값이 없으면 기본값 반환
        return optional.map(UsersEntity::getEmail).orElse(null);
    }

    /**
     * US-07 비밀번호 찾기 (임시 비밀번호 발급)
     * 이메일과 연락처로 본인 확인 후 임시 비밀번호 발급
     *
     * @param email 이메일
     * @param phone 연락처
     * @return String 임시 비밀번호 (실패 시 null)
     */
    public String findPassword(String email, String phone) {
        // 1. 이메일로 사용자 조회
        Optional<UsersEntity> optional = usersRepository.findByEmail(email);

        // 2. 이메일 존재 여부 확인
        if (optional.isPresent()) {
            UsersEntity usersEntity = optional.get();

            // 3. 연락처 일치 확인 (본인 인증)
            if (usersEntity.getPhone().equals(phone)) {
                // 4. 임시 비밀번호 생성
                // 현재 시간을 이용해 고유한 임시 비밀번호 생성
                String tempPassword = "Temp" + System.currentTimeMillis();

                // 5. 임시 비밀번호로 변경
                // TODO: BCrypt 등으로 암호화하여 저장해야 함
                usersEntity.setPassword(tempPassword);
                usersRepository.save(usersEntity);

                // 6. 임시 비밀번호 반환
                // TODO: 실제로는 이메일로 발송해야 함
                return tempPassword;
            }
        }

        return null;
    }

    /**
     * US-08 내 정보 수정
     * 사용자가 자신의 정보를 수정
     *
     * @param usersDto 수정할 정보가 담긴 DTO
     * @return UsersDto 수정된 사용자 정보 DTO
     */
    public UsersDto updateMyInfo(UsersDto usersDto) {
        // 1. 기존 사용자 조회
        Optional<UsersEntity> optional = usersRepository.findById(usersDto.getUserId());

        // 존재 여부 확인
        if (optional.isPresent()) {
            UsersEntity usersEntity = optional.get();

            // 2. 수정 가능한 필드만 업데이트
            // 이메일은 변경할 수 없으니 제외
            // null 체크 후 값이 있는 경우에만 업데이트
            if (usersDto.getPassword() != null) {
                usersEntity.setPassword(usersDto.getPassword());
            }
            if (usersDto.getUserName() != null) {
                usersEntity.setUserName(usersDto.getUserName());
            }
            if (usersDto.getPhone() != null) {
                usersEntity.setPhone(usersDto.getPhone());
            }
            if (usersDto.getAddr() != null) {
                usersEntity.setAddr(usersDto.getAddr());
            }
            if (usersDto.getAddrDetail() != null) {
                usersEntity.setAddrDetail(usersDto.getAddrDetail());
            }

            // 3. 저장 (UPDATE 쿼리 실행)
            // @Transactional 있으면 save() 호출 없이도 자동 업데이트됨 (Dirty Checking)
            // 하지만 명시적으로 save() 호출하는 것이 더 명확함
            UsersEntity updateEntity = usersRepository.save(usersEntity);

            // 4. DTO로 변환하여 반환
            return updateEntity.toDto();
        }

        return null;
    }

    /**
     * US-09 내 정보 조회
     * 사용자 ID로 사용자 정보 조회
     *
     * @param userId 사용자 ID
     * @return UsersDto 사용자 정보 DTO
     */
    public UsersDto getMyInfo(int userId) {
        // 1. ID로 사용자 조회
        Optional<UsersEntity> optional = usersRepository.findById(userId);

        // 2. Entity를 DTO로 변환하여 반환
        // map과 orElse를 사용한 함수형 프로그래밍 방식
        return optional.map(UsersEntity::toDto).orElse(null);
    }

    /**
     * US-10 회원 탈퇴 (논리적 삭제)
     * 실제 데이터를 삭제하지 않고 상태만 변경 (Soft Delete)
     *
     * @param userId 사용자 ID
     * @return boolean 탈퇴 성공 여부
     */
    public boolean deleteUserState(int userId) {
        // 1. 사용자 조회
        Optional<UsersEntity> optional = usersRepository.findById(userId);

        if (optional.isPresent()) {
            UsersEntity usersEntity = optional.get();

            // 2. 상태를 -1(삭제)로 변경 (논리적 삭제)
            // 물리적 삭제보다 안전: 데이터 복구 가능, 통계 유지
            // userState: -1(탈퇴), 0(휴면), 1(정상)
            usersEntity.setUserState(-1);
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
     * 오늘 날짜로 출석 기록 생성 (중복 출석 방지)
     *
     * @param userId 출석할 사용자 ID
     * @return AtenDto 출석 정보 DTO (중복 출석 시 null)
     */
    public AtenDto aten(int userId) {
        // 1. 사용자 조회
        Optional<UsersEntity> userOptional = usersRepository.findById(userId);

        if (userOptional.isPresent()) {
            UsersEntity usersEntity = userOptional.get();
            LocalDate today = LocalDate.now();  // 오늘 날짜

            // 2. 오늘 이미 출석했는지 확인
            Optional<AtenEntity> existingAten =
                    atenRepository.findByUsersEntityAndAtenDate(usersEntity, today);

            if (existingAten.isPresent()) {
                // 3. 이미 출석했으면 null 반환 (중복 출석 방지)
                return null;
            }

            // 4. 새로운 출석 기록 생성
            // Builder 패턴 사용: 가독성 좋고 불변 객체 생성에 유리
            AtenEntity atenEntity = AtenEntity.builder()
                    .atenDate(today)
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
     * 사용자의 모든 출석 기록을 조회
     *
     * @param userId 사용자 ID
     * @return List<AtenDto> 출석 기록 DTO 리스트
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
            // map(): 각 Entity를 DTO로 변환
            // collect(): Stream을 List로 수집
            return atenList.stream()
                    .map(AtenEntity::toDto)
                    .collect(Collectors.toList());
        }

        // 사용자가 없으면 빈 배열 반환
        return new ArrayList<>();
    }

    /**
     * FR-01 친구 요청
     * 다른 사용자에게 친구 요청 전송
     *
     * @param offerUserId    요청하는 사용자 ID
     * @param receiverUserId 요청받는 사용자 ID
     * @return FrenDto 친구 요청 정보 DTO (실패 시 null)
     */
    public FrenDto offerFren(int offerUserId, int receiverUserId) {
        // 1. 자기 자신에게는 친구 요청 불가
        if (offerUserId == receiverUserId) {
            return null;
        }

        // 2. 두 사용자 조회
        Optional<UsersEntity> offerUserOpt = usersRepository.findById(offerUserId);
        Optional<UsersEntity> receiverUserOpt = usersRepository.findById(receiverUserId);

        // 3. 두 사용자 모두 존재하는지 확인
        if (offerUserOpt.isPresent() && receiverUserOpt.isPresent()) {
            UsersEntity offerUser = offerUserOpt.get();
            UsersEntity receiverUser = receiverUserOpt.get();

            // 4. 이미 친구 관계가 존재하는지 확인 (양방향 확인)
            // A→B 요청이든 B→A 요청이든 모두 확인
            Optional<FrenEntity> existing = frenRepository.findFriendship(offerUser, receiverUser);

            if (existing.isPresent()) {
                // 이미 관계가 존재하면 null 반환
                return null;
            }

            // 5. 새로운 친구 요청 생성 (초기 상태: 0 = 대기중)
            // frenState: 0(대기), 1(친구), -1(삭제)
            FrenEntity frenEntity = FrenEntity.builder()
                    .frenState(0)          // 0: 친구 신청 중
                    .offerUser(offerUser)      // 요청한 사용자
                    .receiverUser(receiverUser) // 받은 사용자
                    .build();

            // 6. 저장
            FrenEntity savedEntity = frenRepository.save(frenEntity);

            // 7. DTO로 변환하여 반환
            return savedEntity.toDto();
        }

        return null;
    }

    /**
     * FR-02 친구 수락
     * 받은 친구 요청을 수락
     *
     * @param frenId         친구 관계 ID
     * @param receiverUserId 수락하는 사용자 ID (보안 확인용)
     * @return FrenDto 수락된 친구 정보 DTO
     */
    public FrenDto receiveFren(int frenId, int receiverUserId) {
        // 1. 사용자 조회
        Optional<UsersEntity> receiverUserOpt = usersRepository.findById(receiverUserId);

        if (receiverUserOpt.isPresent()) {
            UsersEntity receiverUser = receiverUserOpt.get();

            // 2. 친구 요청 조회 (나에게 온 요청인지 확인)
            // 다른 사람의 친구 요청을 수락하는 것을 방지
            Optional<FrenEntity> frenOpt =
                    frenRepository.findByFrenIdAndReceiverUser(frenId, receiverUser);

            if (frenOpt.isPresent()) {
                FrenEntity frenEntity = frenOpt.get();

                // 3. 상태를 1(친구)로 변경
                frenEntity.setFrenState(1);

                // 4. 저장 (UPDATE)
                // @Transactional로 인해 자동 업데이트되지만 명시적으로 save() 호출
                FrenEntity updatedEntity = frenRepository.save(frenEntity);

                return updatedEntity.toDto();
            }
        }

        return null;
    }

    /**
     * FR-03 친구 거절
     * 받은 친구 요청을 거절 (관계 자체를 삭제)
     *
     * @param frenId         친구 관계 ID
     * @param receiverUserId 거절하는 사용자 ID
     * @return boolean 거절 성공 여부
     */
    public boolean negativeFren(int frenId, int receiverUserId) {
        // 1. 사용자 조회
        Optional<UsersEntity> receiverUserOpt = usersRepository.findById(receiverUserId);

        if (receiverUserOpt.isPresent()) {
            UsersEntity receiverUser = receiverUserOpt.get();

            // 2. 친구 요청 조회 (나에게 온 요청인지 확인)
            Optional<FrenEntity> frenOpt =
                    frenRepository.findByFrenIdAndReceiverUser(frenId, receiverUser);

            if (frenOpt.isPresent()) {
                // 3. 삭제 (거절은 관계 자체를 삭제)
                // 물리적 삭제: 데이터베이스에서 완전히 제거
                frenRepository.deleteById(frenId);
                return true;
            }
        }

        return false;
    }

    /**
     * FR-04 친구 전체조회
     * 현재 친구 관계인 사용자 목록 조회
     *
     * @param userId 사용자 ID
     * @return List<FrenDto> 친구 목록 DTO 리스트 (상태가 1인 친구만)
     */
    public List<FrenDto> getFren(int userId) {
        // 1. 사용자 조회
        Optional<UsersEntity> userOpt = usersRepository.findById(userId);

        if (userOpt.isPresent()) {
            UsersEntity user = userOpt.get();

            // 2. 상태가 1(현재 친구)인 모든 친구 관계 조회
            // A→B든 B→A든 상관없이 해당 사용자가 포함된 모든 친구 관계 조회
            List<FrenEntity> frenList =
                    frenRepository.findAllFriendByUserAndState(user, 1);

            // 3. Entity 리스트를 DTO 리스트로 변환
            return frenList.stream()
                    .map(FrenEntity::toDto)
                    .collect(Collectors.toList());
        }

        // 사용자가 없으면 빈 배열 반환
        return new ArrayList<>();
    }

    /**
     * FR-05 친구 상세조회
     * 특정 친구 관계의 상세 정보 조회
     *
     * @param frenId 친구 관계 ID
     * @return FrenDto 친구 상세 정보 DTO
     */
    public FrenDto getDetailFren(int frenId) {
        // 1. 친구 관계 조회
        Optional<FrenEntity> frenOpt = frenRepository.findById(frenId);

        // 2. DTO로 변환하여 반환
        return frenOpt.map(FrenEntity::toDto).orElse(null);
    }

    /**
     * FR-06 친구 삭제 (논리적 삭제)
     * 친구 관계를 삭제 상태로 변경
     *
     * @param frenId 친구 관계 ID
     * @param userId 삭제 요청한 사용자 ID (권한 확인용)
     * @return boolean 삭제 성공 여부
     */
    public boolean deleteFren(int frenId, int userId) {
        // 1. 친구 관계 조회
        Optional<FrenEntity> frenOpt = frenRepository.findById(frenId);

        if (frenOpt.isPresent()) {
            FrenEntity frenEntity = frenOpt.get();

            // 2. 권한 확인: 요청자가 해당 친구 관계 당사자인지
            // offerUser나 receiverUser 중 하나여야 삭제 가능
            boolean isAuthorized =
                    frenEntity.getOfferUser().getUserId() == userId ||
                            frenEntity.getReceiverUser().getUserId() == userId;

            if (isAuthorized) {
                // 3. 상태를 -1(삭제)로 변경 (논리적 삭제)
                // 물리적 삭제 대신 상태만 변경하여 복구 가능하게 함
                frenEntity.setFrenState(-1);
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
     * 설정 업데이트 공통 로직 - 헬퍼 메서드
     * 중복 코드를 줄이기 위한 private 메서드
     * 함수형 프로그래밍의 고차 함수(Higher-Order Function) 패턴 사용
     *
     * @param userId 사용자 ID
     * @param updater 업데이트 함수 (람다식으로 전달)
     * @return boolean 업데이트 성공 여부
     */
    private boolean updateSetting(int userId, java.util.function.Consumer<SetEntity> updater) {
        // 1. 사용자 조회
        Optional<UsersEntity> userOpt = usersRepository.findById(userId);

        if (userOpt.isPresent()) {
            UsersEntity user = userOpt.get();

            // 2. 설정 조회 또는 생성
            // 설정이 없으면 기본값으로 새로 생성
            SetEntity setEntity = setRepository.findByUsersEntity(user)
                    .orElseGet(() -> SetEntity.builder()
                            .usersEntity(user)
                            .build());

            // 3. 업데이트 적용 (람다 함수 실행)
            // Consumer: 하나의 인자를 받아 처리하고 반환값이 없는 함수형 인터페이스
            // accept(): Consumer의 추상 메서드
            updater.accept(setEntity);

            // 4. 저장
            setRepository.save(setEntity);
            return true;
        }

        return false;
    }

    /**
     * ST-01 약속 리마인드 설정
     * 약속 시작 전 알림을 받을 시간 설정
     *
     * @param userId 사용자 ID
     * @param remindMinutes 알림 시간 (분 단위, 예: 30분 전)
     * @return boolean 설정 성공 여부
     */
    public boolean setRemind(int userId, int remindMinutes) {
        // 람다식을 사용하여 업데이트 로직 전달
        // entity -> entity.setRemind(remindMinutes)
        return updateSetting(userId, entity -> entity.setSetRemind(remindMinutes));
    }

    /**
     * ST-02 업무표시 설정
     * 캘린더에 업무 일정을 표시할지 여부 설정
     *
     * @param userId 사용자 ID
     * @param workDisplay 업무 표시 여부 (0: 비활성, 1: 활성)
     * @return boolean 설정 성공 여부
     */
    public boolean setWork(int userId, int workDisplay) {
        return updateSetting(userId, entity -> entity.setSetWork(workDisplay));
    }

    /**
     * ST-03 우선교통수단 설정
     * 경로 추천 시 우선적으로 고려할 교통수단 설정
     *
     * @param userId 사용자 ID
     * @param trafficType 교통수단 타입 (SUBWAY_AND_BUS, CAR, BICYCLE, WALK)
     * @return boolean 설정 성공 여부
     */
    public boolean setTraffic(int userId, TrafficType trafficType) {
        return updateSetting(userId, entity -> entity.setSetTraffic(trafficType));
    }

    /**
     * ST-04 언어 설정
     * 앱 인터페이스에 사용할 언어 설정
     *
     * @param userId 사용자 ID
     * @param langType 언어 타입 (KOREAN, ENGLISH, JAPANESE 등)
     * @return boolean 설정 성공 여부
     */
    public boolean setLang(int userId, LangType langType) {
        return updateSetting(userId, entity -> entity.setSetLanguage(langType));
    }

    /**
     * ST-05 설정 초기화
     * 모든 설정을 기본값으로 되돌림
     *
     * @param userId 사용자 ID
     * @return boolean 초기화 성공 여부
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

                // 3. 기본값으로 초기화
                setEntity.setSetRemind(0);                              // 알림 없음
                setEntity.setSetWork(1);                                // 업무 표시 활성
                setEntity.setSetTraffic(TrafficType.SUBWAY_AND_BUS);   // 대중교통
                setEntity.setSetLanguage(LangType.KOREAN);              // 한국어

                // 4. 저장
                setRepository.save(setEntity);
                return true;
            }
        }

        return false;
    }

    /**
     * 설정 조회 (추가 메서드)
     * 사용자의 현재 설정 정보 조회
     *
     * @param userId 사용자 ID
     * @return SetDto 설정 정보 DTO
     */
    public SetDto getSetting(int userId) {
        // 1. 사용자 조회
        Optional<UsersEntity> userOpt = usersRepository.findById(userId);

        if (userOpt.isPresent()) {
            UsersEntity user = userOpt.get();

            // 2. 설정 조회
            Optional<SetEntity> setOpt = setRepository.findByUsersEntity(user);

            if (setOpt.isPresent()) {
                // 설정이 있으면 DTO로 변환하여 반환
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

    // ============================================
    // [향후 구현 예정]
    // ============================================

    /**
     * AU-01 관리자 권한 부여/박탈 (관리자 전용)
     * TODO: 구현 필요
     *
     * @param targetUserId 권한을 변경할 대상 사용자 ID
     * @param adminUserId 관리자 사용자 ID
     * @param grantAdmin 권한 부여 여부 (true: 부여, false: 박탈)
     * @return boolean 변경 성공 여부
     */
    // public boolean updateRole(int targetUserId, int adminUserId, boolean grantAdmin) {
    //     // 1. 관리자 권한 확인
    //     // 2. 대상 사용자 조회
    //     // 3. Role 변경 (USER ↔ ADMIN)
    //     // 4. 저장
    // }
}