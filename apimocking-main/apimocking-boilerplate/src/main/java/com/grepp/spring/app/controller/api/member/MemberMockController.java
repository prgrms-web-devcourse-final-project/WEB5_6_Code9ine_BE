package com.grepp.spring.app.controller.api.member;

import com.grepp.spring.infra.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.context.annotation.Profile;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseCookie;
import jakarta.servlet.http.HttpServletResponse;

// 멤버 로그인 Mock API 컨트롤러 (OAuth + JWT 토큰 사용)
// 입력: MemberLoginRequest(email, password)
// 출력: MemberLoginResponse(code, message, data)
@RestController
@RequestMapping(value = "/api/members", produces = MediaType.APPLICATION_JSON_VALUE)
@Profile("mock")
public class MemberMockController {
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MemberLoginResponse>> login(@RequestBody MemberLoginRequest request, HttpServletResponse response) {
        // OAuth + JWT 토큰 기반 로그인 Mock 응답
        String mockAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwicm9sZXMiOiJST0xFX1VTRVIiLCJqdGkiOiJhY2Nlc3MtdG9rZW4taWQiLCJpYXQiOjE3MzE5MjAwMDAsImV4cCI6MTczMTkyMzYwMH0.mock-signature";
        String mockRefreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwianRpIjoicmVmcmVzaC10b2tlbi1pZCIsImlhdCI6MTczMTkyMDAwMCwiZXhwIjoxNzMxOTI4ODAwfQ.mock-refresh-signature";

        // Set-Cookie 헤더로 accessToken 내려주기
        ResponseCookie cookie = ResponseCookie.from("accessToken", mockAccessToken)
                .httpOnly(true)
                .secure(false) // 개발환경은 false, 운영은 true
                .path("/")
                .maxAge(3600)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        MemberLoginResponse.Data data = new MemberLoginResponse.Data(
                mockAccessToken,
                mockRefreshToken,
                "Bearer",
                3600L,  // access token 만료시간 (1시간)
                28800L  // refresh token 만료시간 (8시간)
        );
        MemberLoginResponse loginResponse = new MemberLoginResponse(2000, "로그인에 성공하였습니다.", data);
        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<MemberSignupResponse>> signup(@RequestBody MemberSignupRequest request) {
        // OAuth 회원가입 Mock 응답
        MemberSignupResponse.Data data = new MemberSignupResponse.Data(1L);
        MemberSignupResponse response = new MemberSignupResponse(2000, "회원가입이 완료되었습니다.", data);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<MemberLogoutResponse>> logout(@RequestBody(required = false) MemberLogoutRequest request) {
        // JWT 토큰 블랙리스트 처리 Mock 응답
        MemberLogoutResponse response = new MemberLogoutResponse(2000, "로그아웃이 완료되었습니다.", null);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/email/send")
    public ResponseEntity<ApiResponse<MemberEmailSendResponse>> sendEmailCode(@RequestBody MemberEmailSendRequest request) {
        // 이메일 인증(코드 발송) Mock 응답
        MemberEmailSendResponse response = new MemberEmailSendResponse(2000, "이메일 인증 코드가 발송되었습니다.", null);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<MemberEmailVerifyResponse>> verifyEmailCode(@RequestBody MemberEmailVerifyRequest request) {
        // 이메일 인증(코드 검증) Mock 응답
        MemberEmailVerifyResponse response = new MemberEmailVerifyResponse(2000, "이메일 인증이 완료되었습니다.", null);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/password/find")
    public ResponseEntity<ApiResponse<MemberPasswordFindResponse>> findPassword(@RequestBody MemberPasswordFindRequest request) {
        // 비밀번호 찾기 Mock 응답
        MemberPasswordFindResponse response = new MemberPasswordFindResponse(2000, "비밀번호 찾기 메일이 발송되었습니다.", null);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<MemberPasswordResetResponse>> resetPassword(@RequestBody MemberPasswordResetRequest request) {
        // 비밀번호 변경 Mock 응답
        MemberPasswordResetResponse response = new MemberPasswordResetResponse(2000, "비밀번호가 변경되었습니다.", null);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/login/kakao")
    public ResponseEntity<ApiResponse<MemberLoginResponse>> kakaoLogin(@RequestBody Map<String, String> request) {
        // OAuth 카카오 로그인 Mock 응답
        String mockKakaoAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwicm9sZXMiOiJST0xFX1VTRVIiLCJwcm92aWRlciI6Imtha2FvIiwianRpIjoia2FrYW8tYWNjZXNzLXRva2VuLWlkIiwiaWF0IjoxNzMxOTIwMDAwLCJleHAiOjE3MzE5MjM2MDB9.mock-kakao-signature";
        String mockKakaoRefreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwicHJvdmlkZXIiOiJrYWthbyIsImp0aSI6Imtha2FvLXJlZnJlc2gtdG9rZW4taWQiLCJpYXQiOjE3MzE5MjAwMDAsImV4cCI6MTczMTkyODgwMH0.mock-kakao-refresh-signature";
        
        MemberLoginResponse.Data data = new MemberLoginResponse.Data(
                mockKakaoAccessToken,
                mockKakaoRefreshToken,
                "Bearer",
                3600L,
                28800L
        );
        MemberLoginResponse response = new MemberLoginResponse(2000, "카카오 로그인에 성공하였습니다.", data);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/login/google")
    public ResponseEntity<ApiResponse<MemberLoginResponse>> googleLogin(@RequestBody Map<String, String> request) {
        // OAuth 구글 로그인 Mock 응답
        String mockGoogleAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwicm9sZXMiOiJST0xFX1VTRVIiLCJwcm92aWRlciI6Imdvb2dsZSIsImp0aSI6Imdvb2dsZS1hY2Nlc3MtdG9rZW4taWQiLCJpYXQiOjE3MzE5MjAwMDAsImV4cCI6MTczMTkyMzYwMH0.mock-google-signature";
        String mockGoogleRefreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwicHJvdmlkZXIiOiJnb29nbGUiLCJqdGkiOiJnb29nbGUtcmVmcmVzaC10b2tlbi1pZCIsImlhdCI6MTczMTkyMDAwMCwiZXhwIjoxNzMxOTI4ODAwfQ.mock-google-refresh-signature";
        
        MemberLoginResponse.Data data = new MemberLoginResponse.Data(
                mockGoogleAccessToken,
                mockGoogleRefreshToken,
                "Bearer",
                3600L,
                28800L
        );
        MemberLoginResponse response = new MemberLoginResponse(2000, "구글 로그인에 성공하였습니다.", data);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<MemberLoginResponse>> refreshToken(@RequestBody MemberTokenRefreshRequest request) {
        // JWT 토큰 갱신 Mock 응답
        String newAccessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwicm9sZXMiOiJST0xFX1VTRVIiLCJqdGkiOiJuZXctYWNjZXNzLXRva2VuLWlkIiwiaWF0IjoxNzMxOTIwMDAwLCJleHAiOjE3MzE5MjM2MDB9.new-mock-signature";
        String newRefreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwianRpIjoibmV3LXJlZnJlc2gtdG9rZW4taWQiLCJpYXQiOjE3MzE5MjAwMDAsImV4cCI6MTczMTkyODgwMH0.new-mock-refresh-signature";
        
        MemberLoginResponse.Data data = new MemberLoginResponse.Data(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                3600L,
                28800L
        );
        MemberLoginResponse response = new MemberLoginResponse(2000, "토큰이 갱신되었습니다.", data);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/mypage")
    public ResponseEntity<ApiResponse<MemberMypageResponse>> getMypage() {
        // 마이페이지 조회 Mock 응답
        MemberMypageRequest.MyPostDto post = new MemberMypageRequest.MyPostDto(1L, "첫 번째 글");
        MemberMypageResponse.Data data = new MemberMypageResponse.Data(
                "test@test.com", "테스트유저", "https://image.url", 1000000, 5, 1200, 2000, 60, List.of(post)
        );
        MemberMypageResponse response = new MemberMypageResponse(2000, "마이페이지 조회 성공", data);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/mypage")
    public ResponseEntity<ApiResponse<MemberMypageResponse>> updateMypage(@RequestBody MemberMypageRequest request) {
        // 마이페이지 수정 Mock 응답
        MemberMypageResponse.Data data = new MemberMypageResponse.Data(
                "test@test.com", request.getName(), request.getProfileImage(), request.getGoalAmount(),
                request.getLevel(), request.getCurrentExp(), request.getNextLevelExp(), request.getExpProgress(), request.getMyPosts()
        );
        MemberMypageResponse response = new MemberMypageResponse(2000, "마이페이지 수정 성공", data);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/profile-image")
    public ResponseEntity<ApiResponse<MemberProfileImageResponse>> updateProfileImage(@RequestBody MemberProfileImageRequest request) {
        // 프로필 이미지 변경 Mock 응답
        MemberProfileImageResponse response = new MemberProfileImageResponse(2000, "프로필 이미지가 변경되었습니다.", null);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/mypage/goal")
    public ResponseEntity<ApiResponse<MemberGoalResponse>> setGoal(@RequestBody MemberGoalRequest request) {
        // 목표 설정 Mock 응답
        MemberGoalResponse.Data data = new MemberGoalResponse.Data(request.getGoalAmount());
        MemberGoalResponse response = new MemberGoalResponse(2000, "목표 금액이 설정되었습니다.", data);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<MemberLogoutResponse>> deleteMember() {
        // 회원 탈퇴 Mock 응답
        MemberLogoutResponse response = new MemberLogoutResponse(2000, "회원 탈퇴가 완료되었습니다.", null);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // === 내부 static DTO 클래스들 ===
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberLoginRequest {
        private String email;
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberLoginResponse {
        private int code;
        private String message;
        private Data data;
        
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Data {
            private String accessToken;
            private String refreshToken;
            private String grantType;
            private Long expiresIn;
            private Long refreshExpiresIn;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberTokenRefreshRequest {
        private String refreshToken;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberSignupRequest {
        private String email;
        private String password;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberSignupResponse {
        private int code;
        private String message;
        private Data data;
        
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Data {
            private Long userId;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MemberLogoutRequest {
        // 로그아웃은 별도의 요청 필드가 없습니다.
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberLogoutResponse {
        private int code;
        private String message;
        private Object data;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberEmailSendRequest {
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberEmailSendResponse {
        private int code;
        private String message;
        private Object data;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberEmailVerifyRequest {
        private String email;
        private String code;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberEmailVerifyResponse {
        private int code;
        private String message;
        private Object data;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberPasswordFindRequest {
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberPasswordFindResponse {
        private int code;
        private String message;
        private Object data;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberPasswordResetRequest {
        private String email;
        private String newPassword;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberPasswordResetResponse {
        private int code;
        private String message;
        private Object data;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberMypageRequest {
        private String name;
        private String profileImage;
        private int goalAmount;
        private int level;
        private int currentExp;
        private int nextLevelExp;
        private int expProgress;
        private List<MyPostDto> myPosts;
        
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MyPostDto {
            private Long postId;
            private String title;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberMypageResponse {
        private int code;
        private String message;
        private Data data;
        
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Data {
            private String email;
            private String name;
            private String profileImage;
            private int goalAmount;
            private int level;
            private int currentExp;
            private int nextLevelExp;
            private int expProgress;
            private List<MemberMypageRequest.MyPostDto> myPosts;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberProfileImageRequest {
        private String profileImage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberProfileImageResponse {
        private int code;
        private String message;
        private Object data;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberGoalRequest {
        private int goalAmount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberGoalResponse {
        private int code;
        private String message;
        private Data data;
        
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Data {
            private int goalAmount;
        }
    }
} 