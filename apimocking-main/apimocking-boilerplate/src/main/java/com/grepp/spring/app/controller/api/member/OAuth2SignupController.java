package com.grepp.spring.app.controller.api.member;

import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.app.model.auth.dto.TokenDto;
import com.grepp.spring.app.model.auth.token.RefreshTokenService;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.app.model.member.dto.OAuth2SignupRequest;
import com.grepp.spring.app.model.member.dto.OAuth2SignupResponse;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
import com.grepp.spring.infra.config.security.UserDetailsServiceImpl;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import com.grepp.spring.infra.error.exceptions.CommonException;
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
        
        log.info("OAuth2 회원가입 요청: email={}, name={}, nickname={}, phoneNumber={}, profileImage={}", 
                request.getEmail(), request.getName(), request.getNickname(), request.getPhoneNumber(), request.getProfileImage());
        
        // 요청 데이터 검증
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            log.error("이메일이 비어있음");
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            log.error("이름이 비어있음");
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }
        if (request.getNickname() == null || request.getNickname().trim().isEmpty()) {
            log.error("닉네임이 비어있음");
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }
        
        try {
            // 이메일 중복 체크
            if (memberRepository.existsByEmailIgnoreCase(request.getEmail())) {
                log.warn("이메일 중복: {}", request.getEmail());
                throw new CommonException(ResponseCode.EMAIL_ALREADY_EXISTS);
            }
            
            // 닉네임 중복 체크
            if (memberRepository.findAll().stream().anyMatch(m -> m.getNickname().equalsIgnoreCase(request.getNickname()))) {
                log.warn("닉네임 중복: {}", request.getNickname());
                throw new CommonException(ResponseCode.NICKNAME_ALREADY_EXISTS);
            }
        
        // 회원 생성
        Member member = new Member();
        member.setEmail(request.getEmail());
        member.setName(request.getName());
        member.setNickname(request.getNickname());
        member.setPhoneNumber(request.getPhoneNumber()); // 전화번호 설정
        member.setProfileImage(request.getProfileImage());
        member.setRole("ROLE_USER"); // 기본 역할 설정
        member.setActivated(true);
        
        // OAuth2 사용자는 비밀번호가 필요 없으므로 임시 값 설정
        // 실제로는 소셜 로그인으로만 인증하므로 이 비밀번호는 사용되지 않음
        member.setPassword("OAUTH2_USER_" + System.currentTimeMillis());
        
        // 데이터베이스 저장 시도
        Member savedMember;
        try {
            savedMember = memberRepository.save(member);
            log.info("회원 저장 성공: {}", savedMember.getEmail());
        } catch (Exception e) {
            log.error("회원 저장 실패: {}", e.getMessage(), e);
            throw new CommonException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
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
        } catch (CommonException e) {
            log.warn("OAuth2 회원가입 검증 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("OAuth2 회원가입 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new CommonException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    private TokenDto generateTokenDto(Member member) {
        try {
            // Spring Security 권한 형식으로 권한 생성
            List<SimpleGrantedAuthority> authorities = userDetailsService.findUserAuthorities(member.getEmail());
            String roles = authorities.stream()
                    .map(authority -> authority.getAuthority())
                    .findFirst()
                    .orElse("ROLE_USER");
            
            // Access Token 생성
            var accessTokenDto = jwtTokenProvider.generateAccessToken(member.getEmail(), roles);
            
                    // Refresh Token 생성 및 저장 (Redis 연결 실패 시 임시 처리)
        RefreshToken refreshToken;
        try {
            refreshToken = refreshTokenService.saveWithAtId(accessTokenDto.getJti());
        } catch (Exception e) {
            log.warn("Redis 연결 실패, 임시 RefreshToken 생성: {}", e.getMessage());
            // Redis 연결 실패 시 임시 토큰 생성
            refreshToken = new RefreshToken(accessTokenDto.getJti());
        }
        
        return TokenDto.builder()
                .accessToken(accessTokenDto.getToken())
                .refreshToken(refreshToken.getToken())
                .grantType("Bearer")
                .expiresIn(3600L)
                .build();
        } catch (Exception e) {
            log.error("토큰 생성 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }


} 