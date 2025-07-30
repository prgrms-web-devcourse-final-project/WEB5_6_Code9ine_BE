# Titae - Spring Boot Backend API Server

**Titae**는 개인 재정 관리 및 챌린지 기반 절약 서비스를 위한 Spring Boot 기반 백엔드 API 서버입니다.

## 🛠 기술 스택

### Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java 21
- **Database**: PostgreSQL
- **Cache**: Redis
- **Security**: Spring Security + JWT
- **OAuth2**: Google OAuth2
- **Build Tool**: Maven
- **Cloud**: AWS (EC2, S3, RDS)

### External Services
- **Email**: Gmail SMTP
- **OCR**: Naver Cloud Platform OCR
- **AI**: Google Gemini API
- **Public Data**: 공공데이터포털 API

### Development Tools
- **IDE**: IntelliJ IDEA
- **API Documentation**: Swagger/OpenAPI 3
- **Version Control**: Git

## 🚀 프로젝트 구조

```
src/main/java/com/grepp/spring/
├── app/
│   ├── controller/api/          # REST API 컨트롤러
│   │   ├── member/             # 회원 관리 API
│   │   ├── auth/               # 인증 관련 API
│   │   ├── budget/             # 예산 관리 API
│   │   ├── community/          # 커뮤니티 API
│   │   └── ...
│   └── model/                  # 도메인 모델
│       ├── member/             # 회원 도메인
│       ├── budget/             # 예산 도메인
│       ├── community/          # 커뮤니티 도메인
│       └── ...
├── infra/                      # 인프라 설정
│   ├── auth/jwt/              # JWT 인증
│   ├── config/                # 설정 클래스
│   ├── response/              # API 응답 형식
│   └── error/                 # 예외 처리
└── util/                      # 유틸리티
```

## 🔧 환경 설정

### 배포 환경
```bash
# 프로덕션 환경 실행
export FRONTEND_URL=https://web-4-5-code9ine-fe-nine.vercel.app
./mvnw spring-boot:run -Dspring.profiles.active=prod
```



## 🔐 인증 시스템

### JWT 토큰 기반 인증
- **Access Token**: 4시간 유효
- **Refresh Token**: 7일 유효
- **HttpOnly 쿠키** 사용으로 보안 강화

### OAuth2 소셜 로그인
- **Google OAuth2** 지원
- 자동 회원가입 및 로그인 처리
- 토큰 자동 갱신



## 🗄 데이터베이스

- **PostgreSQL** 사용
- **JPA/Hibernate** ORM
- **Redis** 캐싱 및 세션 관리

## 🔒 보안 설정

### CORS 설정
- 프론트엔드 도메인 허용
- Credentials 포함 요청 지원

### JWT 보안
- HttpOnly 쿠키 사용
- Secure 속성 (HTTPS 환경)
- SameSite=None 설정

## 📝 API 문서

### Swagger UI
- 로컬: `http://localhost:8080/swagger-ui.html`
- 프로덕션: `https://titae.cedartodo.uk/swagger-ui.html`

### API 응답 형식
```json
{
  "code": "2000",
  "message": "성공",
  "data": {
    // 응답 데이터
  }
}
```

## 🚀 배포 정보

- **백엔드 서버**: `https://titae.cedartodo.uk`
- **프론트엔드**: `https://web-4-5-code9ine-fe-nine.vercel.app`
- **데이터베이스**: AWS RDS (PostgreSQL)
- **캐시**: Redis Cloud

## 📞 문의

프로젝트 관련 문의사항이 있으시면 연락주세요
dkswogh0420@gmail.com