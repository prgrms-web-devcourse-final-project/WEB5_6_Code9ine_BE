package com.grepp.spring.app.controller.api.member;

import com.grepp.spring.app.model.member.service.MemberService;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.grepp.spring.app.model.auth.AuthService;
import com.grepp.spring.app.model.auth.dto.TokenDto;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public MemberController(MemberService memberService, MemberRepository memberRepository, PasswordEncoder passwordEncoder, AuthService authService) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@RequestBody @Valid SignupRequest request) {
        // 이메일, 닉네임 중복 체크
        if (memberRepository.existsByEmailIgnoreCase(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ResponseCode.BAD_REQUEST.code(), "이미 사용중인 이메일입니다.", null));
        }
        if (memberRepository.findAll().stream().anyMatch(m -> m.getNickname().equalsIgnoreCase(request.getNickname()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ResponseCode.BAD_REQUEST.code(), "이미 사용중인 닉네임입니다.", null));
        }
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ResponseCode.BAD_REQUEST.code(), "비밀번호가 일치하지 않습니다.", null));
        }
        // 회원 생성
        Member member = new Member();
        member.setEmail(request.getEmail());
        member.setPassword(passwordEncoder.encode(request.getPassword())); // 비밀번호 암호화
        member.setName(request.getName());
        member.setNickname(request.getNickname());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setRole("ROLE_USER");
        member.setActivated(true);
        Long userId = memberService.create(memberService.mapToDTO(member, new com.grepp.spring.app.model.member.model.MemberDTO()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.successToCreate(new SignupResponse(userId)));
    }

    // 이메일 중복확인
    @PostMapping("/email/check")
    public ResponseEntity<ApiResponse<CheckResponse>> checkEmail(@RequestBody CheckEmailRequest request) {
        boolean exists = memberRepository.existsByEmailIgnoreCase(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(new CheckResponse(!exists)));
    }

    // 닉네임 중복확인
    @PostMapping("/nickname/check")
    public ResponseEntity<ApiResponse<CheckResponse>> checkNickname(@RequestBody CheckNicknameRequest request) {
        boolean exists = memberRepository.findAll().stream().anyMatch(m -> m.getNickname().equalsIgnoreCase(request.getNickname()));
        return ResponseEntity.ok(ApiResponse.success(new CheckResponse(!exists)));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        System.out.println("로그인 시도: email=" + request.getEmail() + ", password=" + request.getPassword());
        // AuthService의 signin을 사용하여 JWT 토큰 발급
        com.grepp.spring.app.controller.api.mock.auth.payload.LoginRequest authRequest = new com.grepp.spring.app.controller.api.mock.auth.payload.LoginRequest();
        authRequest.setUsername(request.getEmail());
        authRequest.setPassword(request.getPassword());
        try {
            TokenDto tokenDto = authService.signin(authRequest);
            LoginResponse.Data data = new LoginResponse.Data(
                tokenDto.getAccessToken(),
                tokenDto.getRefreshToken(),
                tokenDto.getGrantType(),
                tokenDto.getExpiresIn(),
                tokenDto.getRefreshExpiresIn()
            );
            return ResponseEntity.ok(ApiResponse.success(new LoginResponse(data)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.BAD_CREDENTIAL.code(), "이메일 또는 비밀번호가 올바르지 않습니다.", null));
        }
    }

    // ===== 내부 DTO 클래스들 =====
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class SignupRequest {
        private String email;
        private String password;
        private String passwordCheck;
        private String name;
        private String nickname;
        private String phoneNumber;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class SignupResponse {
        private Long userId;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CheckEmailRequest {
        private String email;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CheckNicknameRequest {
        private String nickname;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CheckResponse {
        private boolean available;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        @Schema(description = "이메일", example = "test3@test.com")
        private String email;
        @Schema(description = "비밀번호", example = "string")
        private String password;
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class LoginResponse {
        private Data data;
        @Getter @Setter @NoArgsConstructor @AllArgsConstructor
        public static class Data {
            private String accessToken;
            private String refreshToken;
            private String grantType;
            private Long expiresIn;
            private Long refreshExpiresIn;
        }
    }
} 