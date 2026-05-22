-- ============================================
-- 그때만나양 (See You Later Lamb) 초기 데이터
-- ============================================

-- ============================================
-- 1. 사용자 (Users) 데이터
-- ============================================
INSERT INTO users (email, password, userName, phone, addr, addrDetail, point, userState, signupType, role, createDate, updateDate)
VALUES
-- 관리자 계정
('admin@seeyoulaterlamb.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCu', '관리자', '01012345678', '서울시 강남구 테헤란로 427', '위워크타워 10층', 999999, 1, 1, 'ROLE_ADMIN', NOW(), NOW()),

-- 일반 사용자 (10명)
('alpha@test.com', 'pw1234!', 'Alpha', '01011112222', '서울시 강남구 테헤란로 1', '101호', 10000, 1, 1, 'ROLE_USER', NOW(), NOW()),
('bravo@test.com', 'pw1234!', 'Bravo', '01022223333', '서울시 강남구 테헤란로 2', '202호', 5000, 1, 1, 'ROLE_USER', NOW(), NOW()),
('charlie@test.com', 'pw1234!', 'Charlie', '01033334444', '서울시 강남구 테헤란로 3', '303호', 8000, 1, 2, 'ROLE_USER', NOW(), NOW()),
('delta@test.com', 'pw1234!', 'Delta', '01044445555', '서울시 강남구 테헤란로 4', '404호', 3000, 1, 3, 'ROLE_USER', NOW(), NOW()),
('echo@test.com', 'pw1234!', 'Echo', '01055556666', '서울시 강남구 테헤란로 5', '505호', 12000, 1, 4, 'ROLE_USER', NOW(), NOW()),
('foxtrot@test.com', 'pw1234!', 'Foxtrot', '01066667777', '서울시 강남구 테헤란로 6', '606호', 2000, 1, 1, 'ROLE_USER', NOW(), NOW()),
('golf@test.com', 'pw1234!', 'Golf', '01077778888', '서울시 강남구 테헤란로 7', '707호', 15000, 1, 2, 'ROLE_USER', NOW(), NOW()),
('hotel@test.com', 'pw1234!', 'Hotel', '01088889999', '서울시 강남구 테헤란로 8', '808호', 1000, 1, 3, 'ROLE_USER', NOW(), NOW()),
('india@test.com', 'pw1234!', 'India', '01099990000', '서울시 강남구 테헤란로 9', '909호', 6000, 1, 4, 'ROLE_USER', NOW(), NOW()),
('juliet@test.com', 'pw1234!', 'Juliet', '01012341234', '서울시 강남구 테헤란로 10', '1001호', 20000, 1, 1, 'ROLE_USER', NOW(), NOW());


-- ============================================
-- 2. 친구 관계 (Fren) 데이터
-- ============================================
INSERT INTO fren (userId, frenId, frenState, createDate, updateDate)
VALUES
-- Alpha(2)의 친구들
(2, 3, 1, NOW(), NOW()),  -- Alpha ↔ Bravo
(2, 4, 1, NOW(), NOW()),  -- Alpha ↔ Charlie
(2, 5, 1, NOW(), NOW()),  -- Alpha ↔ Delta

-- Bravo(3)의 친구들
(3, 4, 1, NOW(), NOW()),  -- Bravo ↔ Charlie
(3, 6, 1, NOW(), NOW()),  -- Bravo ↔ Echo

-- Charlie(4)의 친구들
(4, 7, 1, NOW(), NOW()),  -- Charlie ↔ Foxtrot

-- Echo(6)의 친구들
(6, 8, 1, NOW(), NOW()),  -- Echo ↔ Golf
(6, 9, 1, NOW(), NOW());  -- Echo ↔ Hotel


-- ============================================
-- 3. 약속 (Prom) 데이터
-- ============================================
INSERT INTO prom (userId, promTitle, promCont, promDate, promTime, alertTime, location, memo, promState, trafficType, createDate, updateDate)
VALUES
-- Alpha의 약속
(2, '스터디 모임', '주간 알고리즘 스터디', '2026-01-15', '19:00', 60, '강남역 스터디 카페', '노트북 챙기기', 1, 1, NOW(), NOW()),
(2, '점심 약속', 'Bravo랑 점심', '2026-01-12', '12:30', 30, '역삼역 파스타집', '', 1, 2, NOW(), NOW()),
(2, '헬스장', '주 3회 운동', '2026-01-13', '18:00', 30, '집 근처 헬스장', '', 1, 3, NOW(), NOW()),

-- Bravo의 약속
(3, '팀 회의', '프로젝트 중간 점검', '2026-01-14', '10:00', 30, '회사 회의실', '', 1, 1, NOW(), NOW()),
(3, '저녁 모임', 'Charlie, Delta와 저녁', '2026-01-16', '19:30', 60, '선릉역 고기집', '', 1, 2, NOW(), NOW()),

-- Charlie의 약속
(4, '치과 예약', '스케일링', '2026-01-17', '15:00', 120, '강남 치과', '보험증 챙기기', 1, 1, NOW(), NOW()),
(4, '영화 관람', 'Foxtrot과 영화보기', '2026-01-18', '20:00', 60, 'CGV 강남', '', 1, 2, NOW(), NOW()),

-- Delta의 약속
(5, '독서 모임', '월간 북클럽', '2026-01-19', '14:00', 60, '삼성역 카페', '책 읽어가기', 1, 3, NOW(), NOW()),

-- Echo의 약속
(6, '병원 진료', '정기 검진', '2026-01-20', '10:30', 120, '강남 세브란스', '', 1, 1, NOW(), NOW()),
(6, '요가 수업', '주 2회 요가', '2026-01-14', '07:00', 30, '집 근처 요가원', '', 1, 3, NOW(), NOW());


-- ============================================
-- 4. 약속 참석자 (Aten) 데이터
-- ============================================
INSERT INTO aten (promId, userId, atenState, createDate, updateDate)
VALUES
-- 약속 1 (Alpha의 스터디)
(1, 2, 1, NOW(), NOW()),  -- Alpha (주최자)
(1, 3, 1, NOW(), NOW()),  -- Bravo
(1, 4, 1, NOW(), NOW()),  -- Charlie

-- 약속 2 (Alpha의 점심)
(2, 2, 1, NOW(), NOW()),  -- Alpha (주최자)
(2, 3, 1, NOW(), NOW()),  -- Bravo

-- 약속 5 (Bravo의 저녁)
(5, 3, 1, NOW(), NOW()),  -- Bravo (주최자)
(5, 4, 1, NOW(), NOW()),  -- Charlie
(5, 5, 1, NOW(), NOW()),  -- Delta

-- 약속 7 (Charlie의 영화)
(7, 4, 1, NOW(), NOW()),  -- Charlie (주최자)
(7, 7, 1, NOW(), NOW());  -- Foxtrot


-- ============================================
-- 5. 목장 (Farm) 데이터
-- ============================================
INSERT INTO farm (userId, farmName, farmBkgd, farmFence, createDate, updateDate)
VALUES
    (2, 'Alpha의 양떼목장', 1, 1, NOW(), NOW()),
    (3, 'Bravo의 푸른초원', 2, 1, NOW(), NOW()),
    (4, 'Charlie의 양농장', 1, 2, NOW(), NOW()),
    (5, 'Delta의 힐링목장', 3, 1, NOW(), NOW()),
    (6, 'Echo의 행복한 양떼', 1, 3, NOW(), NOW()),
    (7, 'Foxtrot의 양목장', 2, 2, NOW(), NOW());


-- ============================================
-- 6. 양 주인 (Owner) 데이터
-- ============================================
INSERT INTO owner (userId, farmId, createDate, updateDate)
VALUES
    (2, 1, NOW(), NOW()),  -- Alpha -> Alpha의 목장
    (3, 2, NOW(), NOW()),  -- Bravo -> Bravo의 목장
    (4, 3, NOW(), NOW()),  -- Charlie -> Charlie의 목장
    (5, 4, NOW(), NOW()),  -- Delta -> Delta의 목장
    (6, 5, NOW(), NOW()),  -- Echo -> Echo의 목장
    (7, 6, NOW(), NOW());  -- Foxtrot -> Foxtrot의 목장


-- ============================================
-- 7. 양 (Lamb) 데이터
-- ============================================
INSERT INTO lamb (ownerId, lambName, lambRank, lambBirth, lambState, createDate, updateDate)
VALUES
-- Alpha의 양들 (ownerId = 1)
(1, '솜이', 3, NOW(), 1, NOW(), NOW()),      -- EPIC
(1, '구름이', 2, NOW(), 1, NOW(), NOW()),    -- RARE
(1, '뭉게', 1, NOW(), 1, NOW(), NOW()),      -- COMMON

-- Bravo의 양들 (ownerId = 2)
(2, '보들이', 2, NOW(), 1, NOW(), NOW()),    -- RARE
(2, '폭신이', 1, NOW(), 1, NOW(), NOW()),    -- COMMON

-- Charlie의 양들 (ownerId = 3)
(3, '양순이', 4, NOW(), 1, NOW(), NOW()),    -- LEGENDARY
(3, '털실이', 2, NOW(), 1, NOW(), NOW()),    -- RARE

-- Delta의 양들 (ownerId = 4)
(4, '메리', 1, NOW(), 1, NOW(), NOW()),      -- COMMON

-- Echo의 양들 (ownerId = 5)
(5, '하얀양', 3, NOW(), 1, NOW(), NOW()),    -- EPIC
(5, '검은양', 2, NOW(), 1, NOW(), NOW()),    -- RARE
(5, '얼룩양', 2, NOW(), 1, NOW(), NOW()),    -- RARE

-- Foxtrot의 양들 (ownerId = 6)
(6, '뽀송이', 1, NOW(), 1, NOW(), NOW());    -- COMMON


-- ============================================
-- 8. 양 특성 (LambChar) 데이터
-- ============================================
INSERT INTO lambChar (lambId, hungry, wool, location, createDate, updateDate)
VALUES
-- Alpha의 양들
(1, 1, 1, 0, NOW(), NOW()),    -- 솜이: 배부름, 털 깨끗, 목장
(2, 0, 0, 0, NOW(), NOW()),    -- 구름이: 보통, 보통, 목장
(3, -1, 1, 0, NOW(), NOW()),   -- 뭉게: 배고픔, 털 깨끗, 목장

-- Bravo의 양들
(4, 1, 0, 1, NOW(), NOW()),    -- 보들이: 배부름, 보통, 울타리
(5, 0, -1, 0, NOW(), NOW()),   -- 폭신이: 보통, 털 많음, 목장

-- Charlie의 양들
(6, 1, 1, 1, NOW(), NOW()),    -- 양순이: 배부름, 털 깨끗, 울타리
(7, 0, 0, 0, NOW(), NOW()),    -- 털실이: 보통, 보통, 목장

-- Delta의 양들
(8, -1, -1, -1, NOW(), NOW()), -- 메리: 배고픔, 털 많음, 늑대 쫓김

-- Echo의 양들
(9, 1, 0, 0, NOW(), NOW()),    -- 하얀양: 배부름, 보통, 목장
(10, 0, 1, 0, NOW(), NOW()),   -- 검은양: 보통, 털 깨끗, 목장
(11, 1, 1, 1, NOW(), NOW()),   -- 얼룩양: 배부름, 털 깨끗, 울타리

-- Foxtrot의 양들
(12, 0, 0, 0, NOW(), NOW());   -- 뽀송이: 보통, 보통, 목장


-- ============================================
-- 9. 목동 (Shep / ShepEntity) 데이터
-- ============================================
INSERT INTO shep (lambId, shepName, shepBirth, shepState, createDate, updateDate)
VALUES
-- 각 양마다 목동 배정
(1, '솜이 목동', NOW(), 1, NOW(), NOW()),
(2, '구름이 목동', NOW(), 1, NOW(), NOW()),
(3, '뭉게 목동', NOW(), 1, NOW(), NOW()),
(4, '보들이 목동', NOW(), 1, NOW(), NOW()),
(5, '폭신이 목동', NOW(), 1, NOW(), NOW()),
(6, '양순이 목동', NOW(), 1, NOW(), NOW()),
(7, '털실이 목동', NOW(), 1, NOW(), NOW()),
(8, '메리 목동', NOW(), 1, NOW(), NOW()),
(9, '하얀양 목동', NOW(), 1, NOW(), NOW()),
(10, '검은양 목동', NOW(), 1, NOW(), NOW()),
(11, '얼룩양 목동', NOW(), 1, NOW(), NOW()),
(12, '뽀송이 목동', NOW(), 1, NOW(), NOW());


-- ============================================
-- 10. 약속 확률 (Prob) 데이터
-- ============================================
INSERT INTO prob (promId, lambAppear, lambDisappear, createDate, updateDate)
VALUES
-- 각 약속에 대한 양 등장/실종 확률
(1, 85, 10, NOW(), NOW()),   -- 스터디 모임: 높은 등장 확률
(2, 90, 5, NOW(), NOW()),    -- 점심 약속: 매우 높은 등장 확률
(3, 80, 15, NOW(), NOW()),   -- 헬스장: 높은 등장 확률
(4, 75, 20, NOW(), NOW()),   -- 팀 회의: 중간 등장 확률
(5, 88, 8, NOW(), NOW()),    -- 저녁 모임: 높은 등장 확률
(6, 70, 25, NOW(), NOW()),   -- 치과 예약: 중간 등장 확률
(7, 92, 3, NOW(), NOW()),    -- 영화 관람: 매우 높은 등장 확률
(8, 85, 10, NOW(), NOW()),   -- 독서 모임: 높은 등장 확률
(9, 65, 30, NOW(), NOW()),   -- 병원 진료: 중간 등장 확률
(10, 95, 2, NOW(), NOW());   -- 요가 수업: 매우 높은 등장 확률


-- ============================================
-- 11. 포인트 정책 (Point) 데이터
-- ============================================
INSERT INTO point (earnPoint, usePoint, cycleType, createDate, updateDate)
VALUES
    (100, 50, 1, NOW(), NOW()),   -- 일일 보상: 100P 획득, 50P 사용
    (500, 200, 2, NOW(), NOW()),  -- 주간 보상: 500P 획득, 200P 사용
    (2000, 1000, 3, NOW(), NOW()); -- 월간 보상: 2000P 획득, 1000P 사용


-- ============================================
-- 12. 포인트 결제 내역 (Pay) 데이터
-- ============================================
INSERT INTO pay (userId, pointId, payType, payAmt, payDate, createDate, updateDate)
VALUES
-- Alpha의 포인트 거래
(2, 1, 1, 100, NOW(), NOW(), NOW()),   -- 획득: 일일 보상
(2, 1, 2, -50, NOW(), NOW(), NOW()),   -- 사용: 양 먹이

-- Bravo의 포인트 거래
(3, 2, 1, 500, NOW(), NOW(), NOW()),   -- 획득: 주간 보상

-- Charlie의 포인트 거래
(4, 1, 1, 100, NOW(), NOW(), NOW()),   -- 획득: 일일 보상
(4, 1, 1, 100, NOW(), NOW(), NOW()),   -- 획득: 일일 보상

-- Echo의 포인트 거래
(6, 3, 1, 2000, NOW(), NOW(), NOW()),  -- 획득: 월간 보상
(6, 1, 2, -200, NOW(), NOW(), NOW());  -- 사용: 양 액세서리


-- ============================================
-- 13. 약속 평가 (Eval) 데이터
-- ============================================
INSERT INTO eval (promId, evalScore, evalCont, createDate, updateDate)
VALUES
-- 완료된 약속에 대한 평가
(1, 5, '정말 유익한 스터디였어요!', NOW(), NOW()),
(2, 4, '맛있는 점심 감사합니다', NOW(), NOW()),
(5, 5, '즐거운 저녁 시간이었습니다', NOW(), NOW()),
(7, 5, '재밌는 영화였어요!', NOW(), NOW());


-- ============================================
-- 14. 작업 내역 (Work) 데이터
-- ============================================
INSERT INTO work (promId, workType, workDate, createDate, updateDate)
VALUES
-- 약속 1의 작업 내역
(1, 1, NOW(), NOW(), NOW()),  -- 약속 생성
(1, 2, NOW(), NOW(), NOW()),  -- 알림 설정

-- 약속 2의 작업 내역
(2, 1, NOW(), NOW(), NOW()),  -- 약속 생성
(2, 3, NOW(), NOW(), NOW()),  -- 참석 확인

-- 약속 5의 작업 내역
(5, 1, NOW(), NOW(), NOW()),  -- 약속 생성
(5, 2, NOW(), NOW(), NOW()),  -- 알림 설정
(5, 4, NOW(), NOW(), NOW());  -- 약속 완료


-- ============================================
-- 15. 설정 (Set) 데이터
-- ============================================
INSERT INTO `set` (userId, alarmYn, langType, createDate, updateDate)
VALUES
    (2, 1, 1, NOW(), NOW()),  -- Alpha: 알림 ON, 한국어
    (3, 1, 1, NOW(), NOW()),  -- Bravo: 알림 ON, 한국어
    (4, 1, 2, NOW(), NOW()),  -- Charlie: 알림 ON, 영어
    (5, 0, 1, NOW(), NOW()),  -- Delta: 알림 OFF, 한국어
    (6, 1, 1, NOW(), NOW()),  -- Echo: 알림 ON, 한국어
    (7, 1, 1, NOW(), NOW());  -- Foxtrot: 알림 ON, 한국어


-- ============================================
-- 16. 템플릿 (Temp) 데이터
-- ============================================
INSERT INTO temp (userId, tempTitle, tempCont, tempLoc, createDate, updateDate)
VALUES
-- Alpha의 약속 템플릿
(2, '주간 스터디', '알고리즘 문제 풀이', '강남역 스터디 카페', NOW(), NOW()),
(2, '헬스장', '근력 운동', '집 근처 헬스장', NOW(), NOW()),

-- Bravo의 약속 템플릿
(3, '팀 미팅', '프로젝트 진행 상황 공유', '회사 회의실', NOW(), NOW()),

-- Charlie의 약속 템플릿
(4, '정기 검진', '병원 방문', '강남 병원', NOW(), NOW());


-- ============================================
-- 17. 캘린더 (Calend) 데이터
-- ============================================
INSERT INTO calend (userId, calendDate, calendMemo, createDate, updateDate)
VALUES
-- Alpha의 캘린더 메모
(2, '2026-01-15', '스터디 준비하기', NOW(), NOW()),
(2, '2026-01-20', '프로젝트 마감일', NOW(), NOW()),

-- Bravo의 캘린더 메모
(3, '2026-01-14', '회의 자료 준비', NOW(), NOW()),

-- Echo의 캘린더 메모
(6, '2026-01-20', '병원 예약 확인', NOW(), NOW());


-- ============================================
-- 18. 공유 (Share) 데이터
-- ============================================
INSERT INTO share (promId, shareUrl, shareDate, createDate, updateDate)
VALUES
-- 공유된 약속들
(1, 'https://seeyoulaterlamb.com/share/abc123', NOW(), NOW(), NOW()),
(5, 'https://seeyoulaterlamb.com/share/def456', NOW(), NOW(), NOW()),
(7, 'https://seeyoulaterlamb.com/share/ghi789', NOW(), NOW(), NOW());


-- ============================================
-- 데이터 삽입 완료
-- ============================================