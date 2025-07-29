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
import jakarta.servlet.http.Cookie;
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
        excludePath.addAll(List.of("/api/auth/refresh")); // 토큰 갱신 엔드포인트 제외
        // /api/members/logout 경로는 제외하지 않음
        String path = request.getRequestURI();
        return excludePath.stream().anyMatch(path::startsWith);
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        System.out.println("[JwtAuthFilter] 요청 URI: " + uri);
        // 추가: Authorization 헤더, 쿠키 값 모두 출력
        String headerToken = request.getHeader("Authorization");
        System.out.println("[JwtAuthFilter] Authorization 헤더: " + headerToken);
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                System.out.println("[JwtAuthFilter] 쿠키: " + c.getName() + "=" + c.getValue());
            }
        } else {
            System.out.println("[JwtAuthFilter] 쿠키 없음");
        }
        String accessToken = jwtTokenProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);
        if (accessToken == null) {
            System.out.println("[JwtAuthFilter] accessToken 없음 - 필터 통과");
            filterChain.doFilter(request, response);
            return;
        }
        System.out.println("[JwtAuthFilter] accessToken 추출: " + accessToken.substring(0, Math.min(20, accessToken.length())) + "...");
        System.out.println("[JwtAuthFilter] accessToken 전체 길이: " + accessToken.length());
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
            boolean refreshSuccess = manageTokenRefresh(accessToken, request, response);
            if (!refreshSuccess) {
                System.out.println("[JwtAuthFilter] 토큰 갱신 실패");
                // 토큰 갱신 실패 시 401 응답
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"code\":\"401\",\"message\":\"토큰이 만료되었습니다. 다시 로그인해주세요.\"}");
                return;
            }
        } catch (Exception e) {
            System.out.println("[JwtAuthFilter] 예외 발생: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
    
    private boolean manageTokenRefresh(
        String accessToken,
        HttpServletRequest request,
        HttpServletResponse response) throws IOException {
        
        try {
            Claims claims  = jwtTokenProvider.getClaims(accessToken);
            if (userBlackListRepository.existsById(claims.getSubject())) {
                return false;
            }
            
            String refreshToken = jwtTokenProvider.resolveToken(request, AuthToken.REFRESH_TOKEN);
            if (refreshToken == null) {
                return false;
            }
            
            RefreshToken rt = refreshTokenService.findByAccessTokenId(claims.getId());
            if(rt == null) return false;
            
            if (!rt.getToken().equals(refreshToken)) {
                userBlackListRepository.save(new UserBlackList(claims.getSubject()));
                return false;
            }
            
            addToken(response, claims, rt);
            return true;
        } catch (Exception e) {
            System.out.println("[JwtAuthFilter] 토큰 갱신 중 오류: " + e.getMessage());
            return false;
        }
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
            newRefreshToken.getTtl() * 1000); // 초를 밀리초로 변환
        
        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }
}
