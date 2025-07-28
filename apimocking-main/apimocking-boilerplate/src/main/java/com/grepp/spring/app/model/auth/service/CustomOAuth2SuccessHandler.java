package com.grepp.spring.app.model.auth.service;

import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.app.model.auth.dto.TokenDto;
import com.grepp.spring.app.model.auth.token.RefreshTokenService;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
import com.grepp.spring.infra.config.security.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberRepository memberRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException {
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        
        log.info("OAuth2 로그인 성공: {}", email);
        
        // 회원 정보 조회
        Optional<Member> memberOpt = memberRepository.findByEmailIgnoreCase(email);
        
        if (memberOpt.isEmpty()) {
            // 신규 회원인 경우 - 추가 정보 입력 페이지로 리다이렉트
            log.info("OAuth2 신규 회원: {} - 추가 정보 입력 페이지로 리다이렉트", email);
            
            // OAuth2User attribute를 안전하게 가져오기
            String name = getAttributeSafely(oAuth2User, "name");
            String picture = getAttributeSafely(oAuth2User, "picture");
            
            String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/login/google")
                    .queryParam("email", email)
                    .queryParam("name", name)
                    .queryParam("profileImage", picture)
                    .queryParam("provider", "google")
                    .build()
                    .encode()
                    .toUriString();
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
            return;
        }
        
        // 기존 회원인 경우 - JWT 토큰 발급 후 /login/googleauth로 리다이렉트
        Member member = memberOpt.get();
        log.info("OAuth2 기존 회원 로그인: {}", email);
        
        // JWT 토큰 생성
        TokenDto tokenDto = generateTokenDto(member);
        
        // 쿠키 설정
        response.addHeader("Set-Cookie", 
            TokenCookieFactory.create(AuthToken.ACCESS_TOKEN.name(), tokenDto.getAccessToken(), tokenDto.getExpiresIn()).toString());
        response.addHeader("Set-Cookie", 
            TokenCookieFactory.create(AuthToken.REFRESH_TOKEN.name(), tokenDto.getRefreshToken(), tokenDto.getExpiresIn()).toString());
        
        // /login/googleauth로 리다이렉트 (토큰 정보를 URL 파라미터로 전달)
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/login/googleauth")
                .queryParam("access_token", tokenDto.getAccessToken())
                .queryParam("refresh_token", tokenDto.getRefreshToken())
                .queryParam("expires_in", tokenDto.getExpiresIn())
                .queryParam("role", member.getRole())
                .build()
                .encode()
                .toUriString();
        
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
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
    
    // OAuth2User attribute를 안전하게 가져오는 헬퍼 메서드
    private String getAttributeSafely(OAuth2User oAuth2User, String attributeName) {
        Object attribute = oAuth2User.getAttribute(attributeName);
        if (attribute == null) {
            return "";
        }
        return attribute.toString();
    }
} 