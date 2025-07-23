package com.grepp.spring.infra.auth.jwt.filter;

import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.app.model.auth.token.RefreshTokenService;
import com.grepp.spring.app.model.auth.token.UserBlackListRepository;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;
import com.grepp.spring.app.model.auth.token.entity.UserBlackList;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
import com.grepp.spring.infra.auth.jwt.dto.AccessTokenDto;
import com.grepp.spring.infra.error.exceptions.CommonException;
import com.grepp.spring.infra.response.ResponseCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final RefreshTokenService refreshTokenService;
    private final UserBlackListRepository userBlackListRepository;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        List<String> excludePath = new ArrayList<>();
        excludePath.addAll(List.of("/auth/signup", "/auth/login",  "/favicon.ico", "/img", "/js","/css","/download"));
        excludePath.addAll(List.of("/error", "/api/member/exists", "/member/signin", "/member/signup"));
        excludePath.addAll(List.of("/api/members/email/send", "/api/members/email/verify", "/api/members/email/status"));
        // 카카오 로그인 관련 경로 제거
        excludePath.addAll(List.of("/api/members/logout"));
        String path = request.getRequestURI();
        return excludePath.stream().anyMatch(path::startsWith);
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        System.out.println("[JwtAuthFilter] 요청 URI: " + uri);
        String accessToken = jwtTokenProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);
        if (accessToken == null) {
            System.out.println("[JwtAuthFilter] accessToken 없음. 필터 통과");
            filterChain.doFilter(request, response);
            return;
        }
        System.out.println("[JwtAuthFilter] accessToken 추출: " + accessToken.substring(0, Math.min(20, accessToken.length())) + "...");
        try {
            if (jwtTokenProvider.validateToken(accessToken, request)) {
                System.out.println("[JwtAuthFilter] accessToken 유효함");
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                if (userBlackListRepository.existsById(authentication.getName())) {
                    System.out.println("[JwtAuthFilter] 블랙리스트 사용자. 필터 통과");
                    filterChain.doFilter(request, response);
                    return;
                }
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("[JwtAuthFilter] SecurityContext에 인증 정보 세팅 완료: " + authentication.getName());
            } else {
                System.out.println("[JwtAuthFilter] accessToken 유효하지 않음");
            }
        } catch (ExpiredJwtException e) {
            System.out.println("[JwtAuthFilter] accessToken 만료. 리프레시 처리 시도");
            manageTokenRefresh(accessToken, request, response);
        } catch (Exception e) {
            System.out.println("[JwtAuthFilter] 예외 발생: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
    
    private void manageTokenRefresh(
        String accessToken,
        HttpServletRequest request,
        HttpServletResponse response) throws IOException {
        
        Claims claims  = jwtTokenProvider.getClaims(accessToken);
        if (userBlackListRepository.existsById(claims.getSubject())) {
            return;
        }
        
        String refreshToken = jwtTokenProvider.resolveToken(request, AuthToken.REFRESH_TOKEN);
        RefreshToken rt = refreshTokenService.findByAccessTokenId(claims.getId());
        
        if(rt == null) return;
        
        if (!rt.getToken().equals(refreshToken)) {
            userBlackListRepository.save(new UserBlackList(claims.getSubject()));
            throw new CommonException(ResponseCode.SECURITY_INCIDENT);
        }
        
        addToken(response, claims, rt);
    }
    
    private void addToken(HttpServletResponse response, Claims claims, RefreshToken refreshToken) {
        String username = claims.getSubject();
        AccessTokenDto newAccessToken = jwtTokenProvider.generateAccessToken(username,
            (String) claims.get("roles"));
        Authentication authentication = jwtTokenProvider.getAuthentication(newAccessToken.getToken());
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        RefreshToken newRefreshToken = refreshTokenService.renewingToken(refreshToken.getAtId(), newAccessToken.getJti());
        
        ResponseCookie accessTokenCookie = TokenCookieFactory.create(AuthToken.ACCESS_TOKEN.name(),
            newAccessToken.getToken(), jwtTokenProvider.getAccessTokenExpiration());
        
        ResponseCookie refreshTokenCookie = TokenCookieFactory.create(
            AuthToken.REFRESH_TOKEN.name(),
            newRefreshToken.getToken(),
            newRefreshToken.getTtl());
        
        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }
}
