package com.grepp.spring.app.controller.api.member;

import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.app.model.auth.dto.TokenDto;
import com.grepp.spring.app.model.auth.token.RefreshTokenService;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
import com.grepp.spring.infra.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;

@RestController
@RequestMapping("/api/members/oauth-signup")
@RequiredArgsConstructor
@Slf4j
public class OAuth2SignupController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @PostMapping
    public ResponseEntity<ApiResponse<OAuth2SignupResponse>> oauth2Signup(
            @RequestBody @Valid OAuth2SignupRequest request,
            HttpServletResponse response) {
        
        log.info("OAuth2 회원가입 요청: {}", request.getEmail());
        
        // 이메일 중복 체크
        if (memberRepository.existsByEmailIgnoreCase(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("4000", "이미 가입된 이메일입니다.", null));
        }
        
        // 닉네임 중복 체크
        if (memberRepository.findAll().stream().anyMatch(m -> m.getNickname().equalsIgnoreCase(request.getNickname()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("4000", "이미 사용중인 닉네임입니다.", null));
        }
        
        // OAuth2 회원 생성
        Member member = new Member();
        member.setEmail(request.getEmail());
        member.setName(request.getName());
        member.setNickname(request.getNickname());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setProfileImage(request.getProfileImage());
        member.setRole("ROLE_USER");
        // OAuth2 회원은 비밀번호가 없으므로 임의의 값으로 설정
        member.setPassword(passwordEncoder.encode("OAUTH2_USER_" + System.currentTimeMillis()));
        
        Member savedMember = memberRepository.save(member);
        log.info("OAuth2 회원가입 완료: {}", savedMember.getEmail());
        
        // JWT 토큰 생성
        TokenDto tokenDto = generateTokenDto(savedMember);
        
        // 토큰 정보를 응답에 포함 (프론트엔드에서 /login/googleauth로 리다이렉트할 때 사용)
        OAuth2SignupResponse signupResponse = new OAuth2SignupResponse(
                2000, 
                "OAuth2 회원가입이 완료되었습니다.",
                new OAuth2SignupResponse.Data(
                        tokenDto.getAccessToken(),
                        tokenDto.getRefreshToken(),
                        tokenDto.getGrantType(),
                        tokenDto.getExpiresIn(),
                        28800L
                )
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(signupResponse));
    }
    
    private TokenDto generateTokenDto(Member member) {
        // Access Token 생성
        var accessTokenDto = jwtTokenProvider.generateAccessToken(member.getEmail(), member.getRole());
        
        // Refresh Token 생성 및 저장
        RefreshToken refreshToken = refreshTokenService.saveWithAtId(accessTokenDto.getJti());
        
        return TokenDto.builder()
                .accessToken(accessTokenDto.getToken())
                .refreshToken(refreshToken.getToken())
                .grantType("Bearer")
                .expiresIn(3600L)
                .build();
    }
    
    // === DTO Classes ===
    @Getter @Setter
    public static class OAuth2SignupRequest {
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        private String email;
        
        @NotBlank(message = "이름은 필수입니다.")
        private String name;
        
        @NotBlank(message = "닉네임은 필수입니다.")
        private String nickname;
        
        private String phoneNumber;
        private String profileImage;
    }
    
    @Getter @Setter @RequiredArgsConstructor
    public static class OAuth2SignupResponse {
        private final int code;
        private final String message;
        private final Data data;
        
        @Getter @Setter @RequiredArgsConstructor
        public static class Data {
            private final String accessToken;
            private final String refreshToken;
            private final String grantType;
            private final Long expiresIn;
            private final Long refreshExpiresIn;
        }
    }
} 