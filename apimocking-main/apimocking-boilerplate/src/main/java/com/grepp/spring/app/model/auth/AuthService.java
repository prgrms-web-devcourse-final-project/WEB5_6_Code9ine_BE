package com.grepp.spring.app.model.auth;

import com.grepp.spring.app.controller.api.mock.auth.payload.LoginRequest;
import com.grepp.spring.app.model.attendance.domain.Attendance;
import com.grepp.spring.app.model.attendance.repos.AttendanceRepository;
import com.grepp.spring.app.model.auth.dto.TokenDto;
import com.grepp.spring.app.model.auth.token.RefreshTokenService;
import com.grepp.spring.app.model.auth.token.UserBlackListRepository;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;
import com.grepp.spring.app.model.challenge.domain.Challenge;
import com.grepp.spring.app.model.challenge.repos.ChallengeRepository;
import com.grepp.spring.app.model.challenge.service.ChallengeService;
import com.grepp.spring.app.model.challenge_count.domain.ChallengeCount;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.dto.AccessTokenDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
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
    private final AttendanceRepository attendanceRepository;
    private final ChallengeService challengeService;
    
    public TokenDto signin(LoginRequest loginRequest) {
        log.info("AuthService.signin 호출됨: username={}", loginRequest.getUsername());
        
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword());
        
        try {
            // loadUserByUsername + password 검증 후 인증 객체 반환
            // 인증 실패 시: AuthenticationException 발생
            Authentication authentication = authenticationManagerBuilder.getObject()
                                                .authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String email = loginRequest.getUsername();
            log.info("인증 성공: username={}", email);

            // 활성화 여부 체크
            Member member = memberRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));
            if (member.getActivated() != null && !member.getActivated()) {
                throw new RuntimeException("비활성화된 계정입니다. 관리자에게 문의하세요.");
            }

            memberRepository.findByEmailIgnoreCase(email).ifPresent(memberEntity -> {
                LocalDate today = LocalDate.now();
                if (memberEntity.getLastLoginedAt() == null || !memberEntity.getLastLoginedAt().isEqual(today)) {
                    memberEntity.setLastLoginedAt(today);
                    memberRepository.save(memberEntity);
                }
            });

            memberRepository.findByEmail(email).ifPresent(memberEntity -> {
                LocalDate today = LocalDate.now();
                boolean alreadyChecked = attendanceRepository.existsByMemberAndDate(memberEntity, today);
                if (!alreadyChecked) {
                    Attendance attendance = new Attendance();
                    attendance.setMember(memberEntity);
                    attendance.setDate(today);
                    attendance.setIsAttended(true);
                    attendanceRepository.save(attendance);
                }
                challengeService.handle_oneMonthChallenge(memberEntity);
            });

        String roles =  String.join(",", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        return processTokenSignin(authentication.getName(), roles);
        } catch (Exception e) {
            log.error("인증 실패: username={}, error={}", loginRequest.getUsername(), e.getMessage(), e);
            throw e;
        }
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
