-- ============================================
-- 그때만나양 (See You Later Lamb) 초기 데이터
-- Enum 값 정확히 수정한 최종 버전
-- ============================================

-- ============================================
-- 1. 사용자 (users) 데이터
-- ============================================
INSERT INTO users (email, password, userName, phone, addr, addrDetail, point, userState, signupType, role, createDate, updateDate)
VALUES ('admin@lamb.com', 'admin1234!', '관리자', '01012345678', '서울시 강남구', '101호', 999999, 1, 1, 'ROLE_ADMIN', NOW(), NOW());

INSERT INTO users (email, password, userName, phone, addr, addrDetail, point, userState, signupType, role, createDate, updateDate)
VALUES ('alpha@test.com', 'pw1234!', 'Alpha', '01011112222', '서울시 강남구', '101호', 10000, 1, 1, 'ROLE_USER', NOW(), NOW());

INSERT INTO users (email, password, userName, phone, addr, addrDetail, point, userState, signupType, role, createDate, updateDate)
VALUES ('bravo@test.com', 'pw1234!', 'Bravo', '01022223333', '서울시 서초구', '202호', 5000, 1, 1, 'ROLE_USER', NOW(), NOW());

INSERT INTO users (email, password, userName, phone, addr, addrDetail, point, userState, signupType, role, createDate, updateDate)
VALUES ('charlie@test.com', 'pw1234!', 'Charlie', '01033334444', '서울시 송파구', '303호', 8000, 1, 2, 'ROLE_USER', NOW(), NOW());


-- ============================================
-- 2. 친구 관계 (friend) 데이터
-- ============================================
INSERT INTO friend (fren_offer, fren_receiver, frenState, createDate, updateDate)
VALUES (2, 3, 1, NOW(), NOW());

INSERT INTO friend (fren_offer, fren_receiver, frenState, createDate, updateDate)
VALUES (2, 4, 1, NOW(), NOW());

INSERT INTO friend (fren_offer, fren_receiver, frenState, createDate, updateDate)
VALUES (3, 4, 1, NOW(), NOW());


-- ============================================
-- 3. 약속 (promise) 데이터
-- ============================================
INSERT INTO promise (user_id, promTitle, promText, promDate, promAlert, promAddr, promMemo, createDate, updateDate)
VALUES (2, '스터디 모임', '주간 알고리즘 스터디', '2026-01-15 19:00:00', 60, '강남역 스터디 카페', '노트북 챙기기', NOW(), NOW());

INSERT INTO promise (user_id, promTitle, promText, promDate, promAlert, promAddr, promMemo, createDate, updateDate)
VALUES (2, '점심 약속', 'Bravo랑 점심', '2026-01-12 12:30:00', 30, '역삼역 파스타집', '', NOW(), NOW());

INSERT INTO promise (user_id, promTitle, promText, promDate, promAlert, promAddr, promMemo, createDate, updateDate)
VALUES (3, '팀 회의', '프로젝트 중간 점검', '2026-01-14 10:00:00', 30, '회사 회의실', '', NOW(), NOW());


-- ============================================
-- 4. 출석 (attendance) 데이터
-- ============================================
INSERT INTO attendance (user_id, atenDate, createDate, updateDate)
VALUES (2, '2026-01-10', NOW(), NOW());

INSERT INTO attendance (user_id, atenDate, createDate, updateDate)
VALUES (2, '2026-01-11', NOW(), NOW());

INSERT INTO attendance (user_id, atenDate, createDate, updateDate)
VALUES (3, '2026-01-10', NOW(), NOW());


-- ============================================
-- 5. 양 특성 (lambChar) 데이터
-- ============================================
INSERT INTO lambChar (charName, charDesc, effectType, effectValue, isActive, createDate, updateDate)
VALUES ('빠른 발걸음', '이동 속도가 빠릅니다', 'speed', '1.5', 1, NOW(), NOW());

INSERT INTO lambChar (charName, charDesc, effectType, effectValue, isActive, createDate, updateDate)
VALUES ('풍성한 털', '털이 풍성하여 추위에 강합니다', 'defense', '1.2', 1, NOW(), NOW());

INSERT INTO lambChar (charName, charDesc, effectType, effectValue, isActive, createDate, updateDate)
VALUES ('겁없는 성격', '늑대를 두려워하지 않습니다', 'courage', '2.0', 1, NOW(), NOW());

INSERT INTO lambChar (charName, charDesc, effectType, effectValue, isActive, createDate, updateDate)
VALUES ('왕성한 식욕', '먹이를 빨리 소화합니다', 'hunger', '1.3', 1, NOW(), NOW());


-- ============================================
-- 6. 양 품종 (lambInfo) 데이터
-- LambRank Enum: COMMON, RARE, SPECIAL, LEGENDARY
-- ============================================
INSERT INTO lambInfo (char_id, lambName, lambInfo, lambRank, lambPath, createDate, updateDate)
VALUES (1, '풍성해양', '털이 풍성한 양 품종입니다', 0, '/images/lamb1.png', NOW(), NOW());

INSERT INTO lambInfo (char_id, lambName, lambInfo, lambRank, lambPath, createDate, updateDate)
VALUES (2, '겁없어양', '용감한 성격의 양 품종입니다', 1, '/images/lamb2.png', NOW(), NOW());

INSERT INTO lambInfo (char_id, lambName, lambInfo, lambRank, lambPath, createDate, updateDate)
VALUES (3, '배고파양', '항상 먹이를 찾는 양 품종입니다', 2, '/images/lamb3.png', NOW(), NOW());

INSERT INTO lambInfo (char_id, lambName, lambInfo, lambRank, lambPath, createDate, updateDate)
VALUES (4, '전기양', '전설적인 힘을 가진 양입니다', 3, '/images/lamb4.png', NOW(), NOW());


-- ============================================
-- 7. 목장 정보 (farmInfo) 데이터
-- ============================================
INSERT INTO farmInfo (user_id, farmName, farmInfo, maxLamb, farmCost, createDate, updateDate)
VALUES (2, '초보 목장', '작지만 아늑한 목장입니다', 10, 0, NOW(), NOW());

INSERT INTO farmInfo (user_id, farmName, farmInfo, maxLamb, farmCost, createDate, updateDate)
VALUES (3, '중급 목장', '넓은 초원이 있는 목장입니다', 20, 5000, NOW(), NOW());

INSERT INTO farmInfo (user_id, farmName, farmInfo, maxLamb, farmCost, createDate, updateDate)
VALUES (4, '고급 목장', '최고급 시설을 갖춘 목장입니다', 30, 10000, NOW(), NOW());


-- ============================================
-- 8. 목장주 (farmOwner) 데이터
-- ============================================
INSERT INTO farmOwner (user_id, farm_id, ownerName, createDate, updateDate)
VALUES (2, 1, 'Alpha의 양떼목장', NOW(), NOW());

INSERT INTO farmOwner (user_id, farm_id, ownerName, createDate, updateDate)
VALUES (3, 2, 'Bravo의 푸른초원', NOW(), NOW());

INSERT INTO farmOwner (user_id, farm_id, ownerName, createDate, updateDate)
VALUES (4, 3, 'Charlie의 프리미엄 목장', NOW(), NOW());


-- ============================================
-- 9. 양치기 (shepherd) - 사용자가 키우는 양
-- ============================================
INSERT INTO shepherd (user_id, lamb_id, shepName, shepHunger, shepFur, shepExist, createDate, updateDate)
VALUES (2, 1, '솜이', 1, 1, 0, NOW(), NOW());

INSERT INTO shepherd (user_id, lamb_id, shepName, shepHunger, shepFur, shepExist, createDate, updateDate)
VALUES (2, 2, '구름이', 0, 0, 0, NOW(), NOW());

INSERT INTO shepherd (user_id, lamb_id, shepName, shepHunger, shepFur, shepExist, createDate, updateDate)
VALUES (3, 2, '보들이', 1, 0, 1, NOW(), NOW());

INSERT INTO shepherd (user_id, lamb_id, shepName, shepHunger, shepFur, shepExist, createDate, updateDate)
VALUES (4, 4, '전설이', 1, 1, 1, NOW(), NOW());


-- ============================================
-- 10. 포인트 정책 (pointPolicy) 데이터
-- ============================================



-- ============================================
-- 11. 포인트 결제 내역 (pointPay) 데이터
-- ============================================

-- ============================================
-- 12. 공유 (share) 데이터
-- ============================================
INSERT INTO share (prom_id, shareToken, shareCheck, shareScore, shareFeedback, createDate, updateDate)
VALUES (1, 'abc12345678901234567', 0, 3, '약속 지켜줘서 고마워양!', NOW(), NOW());

INSERT INTO share (prom_id, shareToken, shareCheck, shareScore, shareFeedback, createDate, updateDate)
VALUES (2, 'def45678901234567890', 0, 3, '약속 지켜줘서 고마워양!', NOW(), NOW());

-- ============================================
-- 13. 임시 사용자 (temp) 데이터
-- ============================================
INSERT INTO temp (tempName, createDate, updateDate)
VALUES ('비회원1', NOW(), NOW());

INSERT INTO temp (tempName, createDate, updateDate)
VALUES ('비회원2', NOW(), NOW());


-- ============================================
-- 14. 평가 (eval) 데이터
-- ============================================
INSERT INTO eval (user_id, share_id, createDate, updateDate)
VALUES (2, 1, NOW(), NOW());

INSERT INTO eval (temp_id, share_id, createDate, updateDate)
VALUES (1, 1, NOW(), NOW());


-- ============================================
-- 15. 등장 확률 (probability) 데이터
-- ============================================
INSERT INTO probability (share_id, probLamb, probWolf, probRare, createDate, updateDate)
VALUES (1, 85, 10, 5, NOW(), NOW());

INSERT INTO probability (share_id, probLamb, probWolf, probRare, createDate, updateDate)
VALUES (2, 90, 5, 10, NOW(), NOW());


-- ============================================
-- 16. 캘린더 (calender) 데이터
-- CycleType Enum: DAILY(0), WEEKLY(1), MONTHLY(2)
-- ============================================
INSERT INTO calender (prom_id, calendCycle, calendStart, calendEnd, createDate, updateDate)
VALUES (1, 1, '2026-01-15 19:00:00', '2026-03-15 19:00:00', NOW(), NOW());

INSERT INTO calender (prom_id, calendCycle, calendStart, calendEnd, createDate, updateDate)
VALUES (3, 1, '2026-01-14 10:00:00', '2026-02-14 10:00:00', NOW(), NOW());


-- ============================================
-- 17. 설정 (setting) 데이터
-- LangType Enum: KOREAN, ENGLISH, CHINESE_SIMPLIFIED, CHINESE_TRADITIONAL, JAPANESE, VIETNAMESE, SPANISH
-- ============================================