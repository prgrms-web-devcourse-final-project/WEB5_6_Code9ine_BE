package com.grepp.spring.app.controller.api.member;

import com.grepp.spring.app.model.member.dto.MemberLoginRequest;
import com.grepp.spring.app.model.member.dto.MemberLoginResponse;
import com.grepp.spring.app.model.member.dto.MemberSignupRequest;
import com.grepp.spring.app.model.member.dto.MemberSignupResponse;
import com.grepp.spring.app.model.member.dto.MemberLogoutRequest;
import com.grepp.spring.app.model.member.dto.MemberLogoutResponse;
import com.grepp.spring.app.model.member.dto.MemberEmailSendRequest;
import com.grepp.spring.app.model.member.dto.MemberEmailSendResponse;
import com.grepp.spring.app.model.member.dto.MemberEmailVerifyRequest;
import com.grepp.spring.app.model.member.dto.MemberEmailVerifyResponse;
import com.grepp.spring.app.model.member.dto.MemberPasswordFindRequest;
import com.grepp.spring.app.model.member.dto.MemberPasswordFindResponse;
import com.grepp.spring.app.model.member.dto.MemberPasswordResetRequest;
import com.grepp.spring.app.model.member.dto.MemberPasswordResetResponse;
import com.grepp.spring.app.model.member.dto.MemberMypageRequest;
import com.grepp.spring.app.model.member.dto.MemberMypageResponse;
import com.grepp.spring.app.model.member.dto.MemberProfileImageRequest;
import com.grepp.spring.app.model.member.dto.MemberProfileImageResponse;
import com.grepp.spring.app.model.member.dto.MemberGoalRequest;
import com.grepp.spring.app.model.member.dto.MemberGoalResponse;
import com.grepp.spring.app.model.auth.AuthService;
import com.grepp.spring.app.model.auth.dto.TokenDto;
import com.grepp.spring.app.controller.api.auth.payload.LoginRequest;
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

import java.util.List;
import java.util.Map;

// 멤버 로그인 Mock API 컨트롤러
// 입력: MemberLoginRequest(email, password)
// 출력: MemberLoginResponse(code, message, data)
@RestController
@RequestMapping(value = "/api/members", produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberMockController {
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MemberLoginResponse>> login(@RequestBody MemberLoginRequest request) {
        // 하드코딩된 토큰 및 응답 데이터
        MemberLoginResponse.Data data = new MemberLoginResponse.Data(
                "mock-access-token",
                "mock-refresh-token"
        );
        MemberLoginResponse response = new MemberLoginResponse(2000, "로그인에 성공하였습니다.", data);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<MemberSignupResponse>> signup(@RequestBody MemberSignupRequest request) {
        // 하드코딩된 회원가입 응답 데이터
        MemberSignupResponse.Data data = new MemberSignupResponse.Data(1L);
        MemberSignupResponse response = new MemberSignupResponse(2000, "회원가입이 완료되었습니다.", data);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<MemberLogoutResponse>> logout(@RequestBody(required = false) MemberLogoutRequest request) {
        // 하드코딩된 로그아웃 응답 데이터
        MemberLogoutResponse response = new MemberLogoutResponse(2000, "로그아웃이 완료되었습니다.", null);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/email/send")
    public ResponseEntity<ApiResponse<MemberEmailSendResponse>> sendEmailCode(@RequestBody MemberEmailSendRequest request) {
        // 하드코딩된 이메일 인증(코드 발송) 응답 데이터
        MemberEmailSendResponse response = new MemberEmailSendResponse(2000, "이메일 인증 코드가 발송되었습니다.", null);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<MemberEmailVerifyResponse>> verifyEmailCode(@RequestBody MemberEmailVerifyRequest request) {
        // 하드코딩된 이메일 인증(코드 검증) 응답 데이터
        MemberEmailVerifyResponse response = new MemberEmailVerifyResponse(2000, "이메일 인증이 완료되었습니다.", null);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/password/find")
    public ResponseEntity<ApiResponse<MemberPasswordFindResponse>> findPassword(@RequestBody MemberPasswordFindRequest request) {
        // 하드코딩된 비밀번호 찾기 응답 데이터
        MemberPasswordFindResponse response = new MemberPasswordFindResponse(2000, "비밀번호 찾기 메일이 발송되었습니다.", null);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<MemberPasswordResetResponse>> resetPassword(@RequestBody MemberPasswordResetRequest request) {
        // 하드코딩된 비밀번호 변경 응답 데이터
        MemberPasswordResetResponse response = new MemberPasswordResetResponse(2000, "비밀번호가 변경되었습니다.", null);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/login/kakao")
    public ResponseEntity<ApiResponse<MemberLoginResponse>> kakaoLogin(@RequestBody Map<String, String> request) {
        // 하드코딩된 소셜 로그인(카카오) 응답 데이터
        MemberLoginResponse.Data data = new MemberLoginResponse.Data(
                "mock-kakao-access-token",
                "mock-kakao-refresh-token"
        );
        MemberLoginResponse response = new MemberLoginResponse(2000, "카카오 로그인에 성공하였습니다.", data);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/mypage")
    public ResponseEntity<ApiResponse<MemberMypageResponse>> getMypage() {
        // 하드코딩된 마이페이지 조회 응답 데이터
        MemberMypageRequest.MyPostDto post = new MemberMypageRequest.MyPostDto(1L, "첫 번째 글");
        MemberMypageResponse.Data data = new MemberMypageResponse.Data(
                "test@test.com", "테스트유저", "https://image.url", 1000000, 5, 1200, 2000, 60, List.of(post)
        );
        MemberMypageResponse response = new MemberMypageResponse(2000, "마이페이지 조회 성공", data);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/mypage")
    public ResponseEntity<ApiResponse<MemberMypageResponse>> updateMypage(@RequestBody MemberMypageRequest request) {
        // 하드코딩된 마이페이지 수정 응답 데이터
        MemberMypageResponse.Data data = new MemberMypageResponse.Data(
                "test@test.com", request.getName(), request.getProfileImage(), request.getGoalAmount(),
                request.getLevel(), request.getCurrentExp(), request.getNextLevelExp(), request.getExpProgress(), request.getMyPosts()
        );
        MemberMypageResponse response = new MemberMypageResponse(2000, "마이페이지 수정 성공", data);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/profile-image")
    public ResponseEntity<ApiResponse<MemberProfileImageResponse>> updateProfileImage(@RequestBody MemberProfileImageRequest request) {
        // 하드코딩된 프로필 이미지 변경 응답 데이터
        MemberProfileImageResponse response = new MemberProfileImageResponse(2000, "프로필 이미지가 변경되었습니다.", null);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/mypage/goal")
    public ResponseEntity<ApiResponse<MemberGoalResponse>> setGoal(@RequestBody MemberGoalRequest request) {
        // 하드코딩된 목표 설정 응답 데이터
        MemberGoalResponse.Data data = new MemberGoalResponse.Data(request.getGoalAmount());
        MemberGoalResponse response = new MemberGoalResponse(2000, "목표 금액이 설정되었습니다.", data);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<MemberLogoutResponse>> deleteMember() {
        // 하드코딩된 회원 탈퇴 응답 데이터 (로그아웃 응답 재사용)
        MemberLogoutResponse response = new MemberLogoutResponse(2000, "회원 탈퇴가 완료되었습니다.", null);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
} 