-- 그때만나양 sqlStart.sql , data.sql 채우기

INSERT INTO users (email, password, userName, phone, addr, addrDetail, userState, signupType, role)
VALUES
('alpha@test.com', 'pw1234!', 'Alpha', '01011112222', '서울시 강남구 테헤란로 1', '101호', 1, 1, 'ROLE_USER'),

('bravo@test.com', 'pw1234!', 'Bravo', '01022223333', '서울시 강남구 테헤란로 2', '202호', 1, 1, 'ROLE_USER'),

('charlie@test.com', 'pw1234!', 'Charlie', '01033334444', '서울시 강남구 테헤란로 3', '303호', 1, 2, 'ROLE_USER'),

('delta@test.com', 'pw1234!', 'Delta', '01044445555', '서울시 강남구 테헤란로 4', '404호', 1, 3, 'ROLE_USER'),

('echo@test.com', 'pw1234!', 'Echo', '01055556666', '서울시 강남구 테헤란로 5', '505호', 1, 4, 'ROLE_USER'),

('foxtrot@test.com', 'pw1234!', 'Foxtrot', '01066667777', '서울시 강남구 테헤란로 6', '606호', 1, 1, 'ROLE_USER'),

('golf@test.com', 'pw1234!', 'Golf', '01077778888', '서울시 강남구 테헤란로 7', '707호', 1, 2, 'ROLE_USER'),

('hotel@test.com', 'pw1234!', 'Hotel', '01088889999', '서울시 강남구 테헤란로 8', '808호', 1, 3, 'ROLE_USER'),

('india@test.com', 'pw1234!', 'India', '01099990000', '서울시 강남구 테헤란로 9', '909호', 1, 4, 'ROLE_USER'),

('juliet@test.com', 'pw1234!', 'Juliet', '01012341234', '서울시 강남구 테헤란로 10', '1001호', 1, 1, 'ROLE_USER');