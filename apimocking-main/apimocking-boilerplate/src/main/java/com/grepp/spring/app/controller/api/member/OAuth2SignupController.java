package com.grepp.spring.app.controller.api.member;

import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.app.model.auth.dto.TokenDto;
import com.grepp.spring.app.model.auth.token.RefreshTokenService;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
import com.grepp.spring.infra.config.security.UserDetailsServiceImpl;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class OAuth2SignupController {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping("/oauth-signup")
    public ResponseEntity<ApiResponse<OAuth2SignupResponse>> oauth2Signup(
            @RequestBody @Valid OAuth2SignupRequest request,
            HttpServletResponse response) {
        
        log.info("OAuth2 회원가입 요청: {}", request.getEmail());
        
        // 이메일 중복 체크
        if (memberRepository.existsByEmailIgnoreCase(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ResponseCode.BAD_REQUEST.code(), "이미 가입된 이메일입니다.", null));
        }
        
        // 닉네임 중복 체크
        if (memberRepository.findAll().stream().anyMatch(m -> m.getNickname().equalsIgnoreCase(request.getNickname()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(ResponseCode.BAD_REQUEST.code(), "이미 사용중인 닉네임입니다.", null));
        }
        
        // 회원 생성
        Member member = new Member();
        member.setEmail(request.getEmail());
        member.setName(request.getName());
        member.setNickname(request.getNickname());
        member.setProfileImage(request.getProfileImage());
        member.setRole("ROLE_USER"); // 기본 역할 설정
        member.setActivated(true);
        
        Member savedMember = memberRepository.save(member);
        log.info("OAuth2 회원가입 완료: {}", savedMember.getEmail());
        
        // JWT 토큰 생성
        TokenDto tokenDto = generateTokenDto(savedMember);
        
        // 쿠키 설정
        response.addHeader("Set-Cookie",
            TokenCookieFactory.create(AuthToken.ACCESS_TOKEN.name(), tokenDto.getAccessToken(), tokenDto.getExpiresIn()).toString());
        response.addHeader("Set-Cookie",
            TokenCookieFactory.create(AuthToken.REFRESH_TOKEN.name(), tokenDto.getRefreshToken(), tokenDto.getExpiresIn()).toString());
        
        OAuth2SignupResponse signupResponse = new OAuth2SignupResponse(
                2000, 
                "OAuth2 회원가입이 완료되었습니다.",
                new OAuth2SignupResponse.Data(
                        tokenDto.getAccessToken(),
                        tokenDto.getRefreshToken(),
                        tokenDto.getGrantType(),
                        tokenDto.getExpiresIn(),
                        28800L,
                        savedMember.getRole()
                )
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(signupResponse));
    }
    
    private TokenDto generateTokenDto(Member member) {
        // Spring Security 권한 형식으로 권한 생성
        List<SimpleGrantedAuthority> authorities = userDetailsService.findUserAuthorities(member.getEmail());
        String roles = authorities.stream()
                .map(authority -> authority.getAuthority())
                .findFirst()
                .orElse("ROLE_USER");
        
        // Access Token 생성
        var accessTokenDto = jwtTokenProvider.generateAccessToken(member.getEmail(), roles);
        
        // Refresh Token 생성 및 저장
        RefreshToken refreshToken = refreshTokenService.saveWithAtId(accessTokenDto.getJti());
        
        return TokenDto.builder()
                .accessToken(accessTokenDto.getToken())
                .refreshToken(refreshToken.getToken())
                .grantType("Bearer")
                .expiresIn(3600L)
                .build();
    }

    // Request/Response DTOs
    @lombok.Data
    public static class OAuth2SignupRequest {
        private String email;
        private String name;
        private String nickname;
        private String profileImage;
    }

    @lombok.Data
    public static class OAuth2SignupResponse {
        private final int code;
        private final String message;
        private final Data data;
        
        @lombok.Data
        public static class Data {
            private final String accessToken;
            private final String refreshToken;
            private final String grantType;
            private final Long expiresIn;
            private final Long refreshExpiresIn;
            private final String role;
        }
    }
} 