# API Mocking Project

Spring Boot 기반의 API 서버 프로젝트입니다.

## 환경 설정

### 로컬 개발 환경
```bash
# 기본 설정 (localhost:3000)
./mvnw spring-boot:run

# 프론트엔드 URL 변경
FRONTEND_URL=http://localhost:3001 ./mvnw spring-boot:run
```

### 배포 환경
```bash
# 환경변수로 프론트엔드 URL 설정
export FRONTEND_URL=https://web-4-5-code9ine-fe-nine.vercel.app
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

## 환경변수

| 변수명 | 설명 | 기본값 | 예시 |
|--------|------|--------|------|
| `FRONTEND_URL` | 프론트엔드 애플리케이션 URL | `http://localhost:3000` | `https://web-4-5-code9ine-fe-nine.vercel.app` |

## OAuth2 설정

Google OAuth2 로그인 후 리다이렉트되는 프론트엔드 URL이 환경변수 `FRONTEND_URL`에 따라 자동으로 설정됩니다.

- 신규 회원: `{FRONTEND_URL}/login/google`
- 기존 회원: `{FRONTEND_URL}/login/googleauth`

## 배포 정보

- **프론트엔드**: [https://web-4-5-code9ine-fe-nine.vercel.app/](https://web-4-5-code9ine-fe-nine.vercel.app/)
- **백엔드**: Spring Boot API 서버