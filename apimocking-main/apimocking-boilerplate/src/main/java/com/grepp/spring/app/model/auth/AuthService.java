package com.grepp.spring.app.model.auth;

import com.grepp.spring.app.controller.api.mock.auth.payload.LoginRequest;
import com.grepp.spring.app.model.auth.dto.TokenDto;
import com.grepp.spring.app.model.auth.token.RefreshTokenService;
import com.grepp.spring.app.model.auth.token.UserBlackListRepository;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.dto.AccessTokenDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserBlackListRepository userBlackListRepository;
    
    public TokenDto signin(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword());
        
        // loadUserByUsername + password 검증 후 인증 객체 반환
        // 인증 실패 시: AuthenticationException 발생
        Authentication authentication = authenticationManagerBuilder.getObject()
                                            .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String email = loginRequest.getUsername();
        log.info("{}!!!!!!!!", email);

        memberRepository.findByEmail(email).ifPresent(member -> {
            LocalDate today = LocalDate.now();
            if (member.getLastLoginedAt() == null || !member.getLastLoginedAt().isEqual(today)) {
                member.setLastLoginedAt(today);
                memberRepository.save(member);
            }
        });

        String roles =  String.join(",", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        return processTokenSignin(authentication.getName(), roles);
    }

    @Transactional(readOnly = true)
    public TokenDto processTokenSignin(String email, String roles) {
        // black list 에 있다면 해제
        userBlackListRepository.deleteById(email);
        
        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        AccessTokenDto accessToken = jwtTokenProvider.generateAccessToken(email, roles);
        RefreshToken refreshToken = refreshTokenService.saveWithAtId(accessToken.getJti());

        return TokenDto.builder()
                   .accessToken(accessToken.getToken())
                   .atId(accessToken.getJti())
                   .refreshToken(refreshToken.getToken())
                   .grantType("Bearer")
                   .refreshExpiresIn(jwtTokenProvider.getRefreshTokenExpiration())
                   .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                   .build();
    }
    
    // 소셜 로그인용 토큰 생성 메서드
    @Transactional(readOnly = true)
    public TokenDto generateTokenForSocialLogin(String email, String roles) {
        return processTokenSignin(email, roles);
    }
    
}
