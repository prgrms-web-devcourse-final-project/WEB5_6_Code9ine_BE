# API 명세서

## 구현 완료된 API

| 기능 | HTTP 메서드 | API Path | Request | Response | Response Code |
|------|-------------|----------|---------|----------|---------------|
| 회원가입 | POST | `/api/users/signup` | `{"email": "test@test.com", "password": "password123!", "passwordCheck": "password123!", "name": "홍길동", "nickname": "길동이", "phoneNumber": "01012345678"}` | `{"code": "2010", "message": "회원가입이 완료되었습니다.", "data": {"userId": 1}}` | `201 CREATED` / `4001 BAD REQUEST` |
| 로그인 | POST | `/api/users/login` | `{"email": "test@test.com", "password": "password123!"}` | `{"code": "2000", "message": "로그인이 완료되었습니다.", "data": {"accessToken": "jwt-access-token", "refreshToken": "jwt-refresh-token", "grantType": "Bearer", "expiresIn": 3600, "refreshExpiresIn": 28800}}` | `200 OK` / `4011 UNAUTHORIZED` |
| 이메일 인증 코드 요청 | POST | `/api/members/email/send` | `{"email": "test@test.com"}` | `{"code": "2000", "message": "이메일 인증 코드가 발송되었습니다.", "data": {}}` | `200 OK` / `500 INTERNAL SERVER ERROR` |
| 이메일 인증 코드 검증 | POST | `/api/members/email/verify` | `{"email": "test@test.com", "code": 123456}` | `{"code": "2000", "message": "이메일 인증이 완료되었습니다.", "data": {}}` | `200 OK` / `400 BAD REQUEST` |
| 이메일 인증 상태 확인 | GET | `/api/members/email/status/{email}` | 없음 | `{"code": "2000", "message": "이메일 인증 상태 조회 성공", "data": {"verified": true}}` | `200 OK` |
| 아이디(이메일) 찾기 | POST | `/api/users/email/find` | `{"name": "홍길동", "phoneNumber": "01012345678"}` | `{"code": "2000", "message": "이메일을 찾았습니다.", "data": {"emails": ["te****@test.com", "an****@test.com"]}}` | `200 OK` / `4041 NOT FOUND` |
| 비밀번호 찾기 | POST | `/api/users/password/find` | `{"email": "test@test.com"}` | `{"code": "2000", "message": "임시 비밀번호를 전송했습니다.", "data": {}}` | `200 OK` / `4041 NOT FOUND` |
| 비밀번호 변경 | PATCH | `/api/users/password` | `{"oldPassword": "password123!", "newPassword": "newpassword456!"}` | `{"code": "2000", "message": "비밀번호가 변경되었습니다.", "data": {}}` | `200 OK` / `4001 BAD REQUEST` |

## Mock 서버만 존재 (실제 구현 필요)

| 기능 | HTTP 메서드 | API Path | Request | Response | Response Code |
|------|-------------|----------|---------|----------|---------------|
| 로그아웃 | POST | `/api/users/logout` | 없음 (Access Token 필요) | `{"code": "2000", "message": "로그아웃이 완료되었습니다.", "data": {}}` | `200 OK` |
| 소셜 회원가입 | - | - | - | - | - |
| 소셜 로그인 | POST | `/api/users/login/kakao` | `{"kakaoToken": "kakao-access-token"}` | `{"code": "2000", "message": "소셜 로그인이 완료되었습니다.", "data": {"accessToken": "jwt-access-token", "refreshToken": "jwt-refresh-token"}}` | `200 OK` / `4011 UNAUTHORIZED` |
| 마이페이지 조회 | GET | `/api/users/mypage` | 없음 (Access Token 필요) | `{"code": "2000", "message": "마이페이지 조회 성공", "data": {"userId": 1, "email": "test@test.com", "name": "테스트유저", "profileImage": "https://image.url", "level": 5}}` | `200 OK` / `4011 UNAUTHORIZED` |

## 주요 변경사항

### 1. 휴대폰번호 형식 통일
- **기존:** `010-1234-5678` (하이픈 포함)
- **변경:** `01012345678` (하이픈 제거)
- **정규식:** `^01[016789]\\d{7,8}$`

### 2. 비밀번호 정책 강화
- **요구사항:** 8자 이상, 영문/숫자/특수문자 포함
- **정규식:** `^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$`

### 3. 이메일 마스킹 처리
- **형식:** `te****@test.com`
- **적용:** 아이디 찾기 API

### 4. 보안 설정
- **인증 없이 접근 가능:** `/api/users/email/find`, `/api/users/password/find`
- **JWT 토큰 기반 인증:** 비밀번호 변경 API 