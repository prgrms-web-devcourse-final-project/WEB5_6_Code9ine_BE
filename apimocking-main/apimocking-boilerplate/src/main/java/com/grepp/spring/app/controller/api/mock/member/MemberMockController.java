package com.grepp.spring.app.controller.api.mock.member;

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
import org.springframework.web.bind.annotation.PathVariable;

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

    // 이메일 인증 코드 발송 (EmailVerificationController 명세 반영)
    @PostMapping("/email/send")
    public ResponseEntity<ApiResponse<EmailSendResponse>> sendEmailCode(@RequestBody EmailSendRequest request) {
        EmailSendResponse response = new EmailSendResponse("이메일 인증 코드가 발송되었습니다.");
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 이메일 인증 코드 검증 (EmailVerificationController 명세 반영)
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<EmailVerifyResponse>> verifyEmailCode(@RequestBody EmailVerifyRequest request) {
        // 항상 성공 응답
        EmailVerifyResponse response = new EmailVerifyResponse("이메일 인증이 완료되었습니다.");
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 이메일 인증 상태 확인 (EmailVerificationController 명세 반영)
    @GetMapping("/email/status/{email}")
    public ResponseEntity<ApiResponse<EmailStatusResponse>> checkEmailStatus(@PathVariable String email) {
        // 항상 인증됨으로 응답
        EmailStatusResponse response = new EmailStatusResponse(true);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // === EmailVerificationController DTO ===
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class EmailSendRequest { private String email; }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class EmailSendResponse { private String message; }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class EmailVerifyRequest { private String email; private String code; }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class EmailVerifyResponse { private String message; }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class EmailStatusResponse { private boolean verified; }

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
        MemberMypageResponse.BookmarkedPostDto bookmarkedPost = new MemberMypageResponse.BookmarkedPostDto(2L, "나만의 가성비 장소");
        MemberMypageResponse.BookmarkedPlaceDto bookmarkedPlace = new MemberMypageResponse.BookmarkedPlaceDto(1L, "착한식당");
        MemberMypageResponse.Data data = new MemberMypageResponse.Data(
                1, "test@test.com", "테스트유저", "https://image.url", 5, 1200, 2000, 60, List.of(post),
                "자동차", new java.math.BigDecimal("10000"),
                List.of(bookmarkedPost), List.of(bookmarkedPlace)
        );
        MemberMypageResponse response = new MemberMypageResponse(2000, "마이페이지 조회 성공", data);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/mypage")
    public ResponseEntity<ApiResponse<MemberMypageResponse>> updateMypage(@RequestBody MemberMypageRequest request) {
        // 마이페이지 수정 Mock 응답
        MemberMypageResponse.Data data = new MemberMypageResponse.Data(
                1,"test@test.com", request.getName(), request.getProfileImage(),
                request.getLevel(), request.getCurrentExp(), request.getNextLevelExp(), request.getExpProgress(), request.getMyPosts(),
                request.getGoalStuff(), request.getRemainPrice(),
                request.getBookmarkedPosts(), request.getBookmarkedPlaces()
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

    // 마이페이지 - 북마크한 게시글 목록 조회
    @GetMapping("/bookmarks/posts")
    public ResponseEntity<ApiResponse<Object>> getBookmarkedPosts() {
        // 실제 명세와 동일한 포맷으로 하드코딩 응답
        Map<String, Object> post = new java.util.HashMap<>();
        post.put("postid", 0);
        post.put("memberId", 1);
        post.put("category", "챌린지");
        post.put("challengeCategory", "카테고리1");
        post.put("title", "게시물 제목0");
        post.put("createdAt", "2025-07-10T11:32:00");
        post.put("content", "게시글 내용0");
        post.put("imageUrls", java.util.List.of("image0.jpg", "image1.jpg"));
        post.put("commentCount", 3);
        post.put("likeCount", 17);
        post.put("isLiked", true);
        post.put("isBookmarked", false);
        post.put("challengeAchieved", true);
        post.put("writerNickname", "작성자 닉네임0");
        post.put("writerTitle", "칭호0");
        post.put("writerLevel", 3);
        post.put("writerProfileImage", "profile0.jpg");
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("code", "0000");
        response.put("message", "북마크한 게시물을 조회하였습니다.");
        response.put("data", java.util.List.of(post));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 마이페이지 - 내가 작성한 게시글 목록 조회
    @GetMapping("/mypage/posts")
    public ResponseEntity<ApiResponse<Object>> getMyPosts() {
        Map<String, Object> post = new java.util.HashMap<>();
        post.put("postid", 0);
        post.put("memberId", 1);
        post.put("category", "챌린지");
        post.put("challengeCategory", "카테고리1");
        post.put("title", "게시물 제목0");
        post.put("createdAt", "2025-07-10T11:32:00");
        post.put("content", "게시글 내용0");
        post.put("imageUrls", java.util.List.of("image0.jpg", "image1.jpg"));
        post.put("commentCount", 3);
        post.put("likeCount", 17);
        post.put("isLiked", true);
        post.put("isBookmarked", false);
        post.put("challengeAchieved", true);
        post.put("writerNickname", "작성자 닉네임0");
        post.put("writerTitle", "칭호0");
        post.put("writerLevel", 3);
        post.put("writerProfileImage", "profile0.jpg");
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("code", "0000");
        response.put("message", "내가 작성한 게시글을 조회하였습니다.");
        response.put("data", java.util.List.of(post));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 마이페이지 - 장소 북마크 목록 조회
    @GetMapping("/bookmarks/places")
    public ResponseEntity<Object> getBookmarkedPlaces() {
        java.util.List<Object> data = new java.util.ArrayList<>();
        java.util.Map<String, Object> store = new java.util.HashMap<>();
        store.put("storeId", "1");
        store.put("name", "서울식당");
        store.put("address", "서울시 강남구 테헤란로 123");
        store.put("categoey", "한식");
        store.put("type", "store");
        store.put("contact", "02-1212-1212");
        store.put("firstmenu", "제육볶음");
        store.put("firstprice", "13000");
        store.put("secondmenu", "김치볶음밥");
        store.put("secondprice", "7000");
        store.put("thirdmenu", "잔치국수");
        store.put("thirdprice", "5000");
        store.put("bookmarkedAt", "2024-01-15T10:30:00");
        data.add(store);
        java.util.Map<String, Object> festival = new java.util.HashMap<>();
        festival.put("festivalId", "1");
        festival.put("name", "강남 축제");
        festival.put("category", "축제테마");
        festival.put("type", "festival");
        festival.put("address", "서울시 강남구 역삼동");
        festival.put("target", "주요고객");
        festival.put("url", "https://festival.example.com");
        festival.put("startAt", "2024-03-01");
        festival.put("EndAt", "2024-03-15");
        festival.put("bookmarkedAt", "2024-01-14T15:20:00");
        data.add(festival);
        java.util.Map<String, Object> library = new java.util.HashMap<>();
        library.put("libraryId", "1");
        library.put("name", "서울도서관");
        library.put("type", "library");
        library.put("address", "서울시 강남구 논현로 50");
        library.put("url", "https://library.example.com");
        library.put("bookmarkedAt", "2024-01-13T09:15:00");
        data.add(library);
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("status", "success");
        result.put("data", data);
        return ResponseEntity.ok(result);
    }

    // 마이페이지 - 장소 북마크 해제
    @PatchMapping("/bookmarks/places/{placeId}")
    public ResponseEntity<ApiResponse<Object>> unbookmarkPlace(@PathVariable String placeId) {
        return ResponseEntity.ok(ApiResponse.success(
            Map.of(
                "code", "2000",
                "message", "장소 북마크를 해제했습니다.",
                "data", Map.of()
            )
        ));
    }

    // 마이페이지 - 프로필 이미지 변경
    @PatchMapping("/mypage/profile-image")
    public ResponseEntity<ApiResponse<Object>> updateProfileImageV2(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(ApiResponse.success(
            Map.of(
                "code", "2000",
                "message", "프로필 이미지가 변경되었습니다.",
                "data", Map.of()
            )
        ));
    }

    // 마이페이지 - 챌린지 대시보드 조회
    @GetMapping("/mypage/challenges/dashboard")
    public ResponseEntity<ApiResponse<Object>> getChallengeDashboard() {
        return ResponseEntity.ok(ApiResponse.success(
            Map.of(
                "code", "2000",
                "message", "챌린지 목록을 조회했습니다.",
                "data", List.of(
                    Map.of(
                        "challengeId", 1,
                        "title", "만원의 행복",
                        "type", "일일",
                        "description", "만원으로 하루 살아보기",
                        "total", 1,
                        "progress", 0,
                        "icon", "moneyIcon"
                    ),
                    Map.of(
                        "challengeId", 2,
                        "title", "영수증 인증하기",
                        "type", "커뮤니티",
                        "description", "오늘 사용한 영수증 인증하기",
                        "total", 1,
                        "progress", 0,
                        "icon", "receipt"
                    )
                )
            )
        ));
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
        private int level;
        private int currentExp;
        private int nextLevelExp;
        private int expProgress;
        private List<MyPostDto> myPosts;
        private String goalStuff; // ex) "자동차"
        private java.math.BigDecimal remainPrice; // ex) 10000
        private List<MemberMypageResponse.BookmarkedPostDto> bookmarkedPosts;
        private List<MemberMypageResponse.BookmarkedPlaceDto> bookmarkedPlaces;
        
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MyPostDto {
            private Long postId;
            private String title;
        }
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BookmarkedPostDto {
            private Long postId;
            private String title;
        }
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BookmarkedPlaceDto {
            private Long placeId;
            private String name;
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
            private int userId;
            private String email;
            private String name;
            private String profileImage;
            private int level;
            private int currentExp;
            private int nextLevelExp;
            private int expProgress;
            private List<MemberMypageRequest.MyPostDto> myPosts;
            private String goalStuff; // ex) "자동차"
            private java.math.BigDecimal remainPrice; // ex) 10000
            private List<BookmarkedPostDto> bookmarkedPosts;
            private List<BookmarkedPlaceDto> bookmarkedPlaces;
        }
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BookmarkedPostDto {
            private Long postId;
            private String title;
        }
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BookmarkedPlaceDto {
            private Long placeId;
            private String name;
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