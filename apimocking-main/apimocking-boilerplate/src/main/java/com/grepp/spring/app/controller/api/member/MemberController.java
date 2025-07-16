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
import com.grepp.spring.app.model.auth.service.EmailVerificationService;
import com.grepp.spring.app.model.auth.service.EmailService;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/users")
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;
    private final EmailService emailService;

    public MemberController(MemberService memberService, MemberRepository memberRepository, PasswordEncoder passwordEncoder, AuthService authService, EmailVerificationService emailVerificationService, EmailService emailService) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.emailVerificationService = emailVerificationService;
        this.emailService = emailService;
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
        
        // 이메일 인증 확인
        if (!emailVerificationService.isEmailVerified(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ResponseCode.BAD_REQUEST.code(), "이메일 인증이 필요합니다.", null));
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
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        System.out.println("로그인 시도: email=" + request.getEmail() + ", password=" + request.getPassword());
        com.grepp.spring.app.controller.api.mock.auth.payload.LoginRequest authRequest = new com.grepp.spring.app.controller.api.mock.auth.payload.LoginRequest();
        authRequest.setUsername(request.getEmail());
        authRequest.setPassword(request.getPassword());
        try {
            TokenDto tokenDto = authService.signin(authRequest);

            // accessToken 쿠키 생성
            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", tokenDto.getAccessToken())
                .httpOnly(true)
                .secure(false) // 운영 환경에서는 true
                .path("/")
                .maxAge(tokenDto.getExpiresIn() / 1000)
                .sameSite("Lax")
                .build();
            // refreshToken 쿠키 생성
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenDto.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(tokenDto.getRefreshExpiresIn() / 1000)
                .sameSite("Lax")
                .build();
            response.addHeader("Set-Cookie", accessTokenCookie.toString());
            response.addHeader("Set-Cookie", refreshTokenCookie.toString());

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

    // 아이디(이메일) 찾기
    @PostMapping("/email/find")
    public ResponseEntity<ApiResponse<FindEmailResponse>> findEmail(@RequestBody @Valid FindEmailRequest request) {
        // 이름과 휴대폰번호로 이메일 찾기 (여러 개 가능)
        java.util.List<Member> members = memberRepository.findByNameAndPhoneNumber(request.getName(), request.getPhoneNumber());
        
        if (members.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(ResponseCode.NOT_FOUND.code(), "해당 정보로 가입된 계정을 찾을 수 없습니다.", null));
        }
        
        // 이메일 마스킹 처리 (보안상 일부만 노출)
        java.util.List<String> maskedEmails = members.stream()
                .map(member -> maskEmail(member.getEmail()))
                .toList();
        
        FindEmailResponse response = new FindEmailResponse(maskedEmails);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 비밀번호 찾기 (임시 비밀번호 발송)
    @PostMapping("/password/find")
    public ResponseEntity<ApiResponse<PasswordFindResponse>> findPassword(@RequestBody @Valid PasswordFindRequest request) {
        // 이메일로 회원 조회
        Member member = memberRepository.findByEmailIgnoreCase(request.getEmail())
                .orElse(null);
        
        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(ResponseCode.NOT_FOUND.code(), "해당 이메일로 가입된 계정을 찾을 수 없습니다.", null));
        }
        
        // 임시 비밀번호 생성 및 발송
        String tempPassword = generateTempPassword();
        member.setPassword(passwordEncoder.encode(tempPassword));
        memberRepository.save(member);
        
        // 이메일 발송
        try {
            emailService.sendTempPasswordEmail(member.getEmail(), tempPassword);
        } catch (Exception e) {
            // 이메일 발송 실패 시 비밀번호 롤백
            member.setPassword(passwordEncoder.encode(generateTempPassword())); // 기존 비밀번호로 복원 불가능하므로 새로운 임시 비밀번호 생성
            memberRepository.save(member);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(ResponseCode.INTERNAL_SERVER_ERROR.code(), "이메일 발송에 실패했습니다. 잠시 후 다시 시도해주세요.", null));
        }
        
        PasswordFindResponse response = new PasswordFindResponse();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<PasswordChangeResponse>> changePassword(@RequestBody @Valid PasswordChangeRequest request) {
        // JWT에서 현재 사용자 이메일 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth != null ? auth.getName() : null;
        if (currentEmail == null || currentEmail.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED.code(), "인증 정보가 유효하지 않습니다.", null));
        }
        Member member = memberRepository.findByEmailIgnoreCase(currentEmail)
                .orElse(null);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(ResponseCode.NOT_FOUND.code(), "사용자를 찾을 수 없습니다.", null));
        }
        // 기존 비밀번호 확인
        if (!passwordEncoder.matches(request.getOldPassword(), member.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ResponseCode.BAD_REQUEST.code(), "기존 비밀번호가 일치하지 않습니다.", null));
        }
        // 비밀번호 정책: 8자 이상, 영문/숫자/특수문자 포함
        if (!isValidPassword(request.getNewPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ResponseCode.BAD_REQUEST.code(), "비밀번호는 8자 이상, 영문/숫자/특수문자를 모두 포함해야 합니다.", null));
        }
        // 새 비밀번호로 변경
        member.setPassword(passwordEncoder.encode(request.getNewPassword()));
        memberRepository.save(member);
        PasswordChangeResponse response = new PasswordChangeResponse();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ===== 유틸리티 메서드 =====
    
    // 이메일 마스킹 처리
    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }
        
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return email;
        }
        
        String localPart = parts[0];
        String domain = parts[1];
        
        if (localPart.length() <= 2) {
            return email;
        }
        
        String maskedLocal = localPart.substring(0, 2) + "*".repeat(localPart.length() - 2);
        return maskedLocal + "@" + domain;
    }
    
    // 임시 비밀번호 생성
    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }

    // 비밀번호 정책 검증
    private boolean isValidPassword(String password) {
        if (password == null) return false;
        // 8자 이상, 영문/숫자/특수문자 포함
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$");
    }

    // ===== 내부 DTO 클래스들 =====
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class SignupRequest {
        @Schema(description = "이메일", example = "test@test.com")
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;
        
        @Schema(description = "비밀번호", example = "password123!")
        @NotBlank(message = "비밀번호는 필수입니다.")
        private String password;
        
        @Schema(description = "비밀번호 확인", example = "password123!")
        @NotBlank(message = "비밀번호 확인은 필수입니다.")
        private String passwordCheck;
        
        @Schema(description = "이름", example = "홍길동")
        @NotBlank(message = "이름은 필수입니다.")
        private String name;
        
        @Schema(description = "닉네임", example = "길동이")
        @NotBlank(message = "닉네임은 필수입니다.")
        private String nickname;
        
        @Schema(description = "휴대폰번호", example = "01012345678")
        @NotBlank(message = "휴대폰번호는 필수입니다.")
        @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "휴대폰번호 형식이 올바르지 않습니다.")
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

    // 아이디(이메일) 찾기 관련 DTO
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class FindEmailRequest {
        @Schema(description = "이름", example = "안재호")
        @NotBlank(message = "이름은 필수입니다.")
        private String name;
        @Schema(description = "휴대폰번호", example = "01012345678")
        @NotBlank(message = "휴대폰번호는 필수입니다.")
        @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "휴대폰번호 형식이 올바르지 않습니다.")
        private String phoneNumber;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class FindEmailResponse {
        @Schema(description = "마스킹된 이메일 목록", example = "[\"te****@test.com\", \"an****@test.com\"]")
        private java.util.List<String> emails;
    }

    // 비밀번호 찾기 관련 DTO
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class PasswordFindRequest {
        @Schema(description = "이메일", example = "test@test.com")
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;
    }

    @Getter @Setter @NoArgsConstructor
    public static class PasswordFindResponse {
        // 응답 데이터 없음 (성공 메시지만 반환)
    }

    // 비밀번호 변경 관련 DTO
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class PasswordChangeRequest {
        @Schema(description = "기존 비밀번호", example = "1234")
        @NotBlank(message = "기존 비밀번호는 필수입니다.")
        private String oldPassword;
        @Schema(description = "새 비밀번호", example = "5678")
        @NotBlank(message = "새 비밀번호는 필수입니다.")
        private String newPassword;
    }

    @Getter @Setter @NoArgsConstructor
    public static class PasswordChangeResponse {
        // 응답 데이터 없음 (성공 메시지만 반환)
    }
} 