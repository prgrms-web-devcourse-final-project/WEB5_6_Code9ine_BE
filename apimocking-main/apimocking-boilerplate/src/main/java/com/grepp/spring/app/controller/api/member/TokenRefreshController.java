package com.grepp.spring.app.controller.api.member;

import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.app.model.auth.token.RefreshTokenService;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;
import com.grepp.spring.app.model.auth.token.entity.UserBlackList;
import com.grepp.spring.app.model.auth.token.UserBlackListRepository;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
import com.grepp.spring.infra.auth.jwt.dto.AccessTokenDto;
import com.grepp.spring.infra.error.exceptions.CommonException;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class TokenRefreshController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserBlackListRepository userBlackListRepository;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        
        log.info("토큰 갱신 요청 시작");
        
        try {
            // 만료된 액세스 토큰 추출
            String expiredAccessToken = jwtTokenProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);
            if (expiredAccessToken == null) {
                log.warn("액세스 토큰이 없음");
                throw new CommonException(ResponseCode.UNAUTHORIZED);
            }

            // 만료된 토큰에서 클레임 추출
            Claims claims;
            try {
                claims = jwtTokenProvider.getClaims(expiredAccessToken);
            } catch (ExpiredJwtException e) {
                claims = e.getClaims();
            }

            // 블랙리스트 체크
            if (userBlackListRepository.existsById(claims.getSubject())) {
                log.warn("블랙리스트 사용자: {}", claims.getSubject());
                throw new CommonException(ResponseCode.UNAUTHORIZED);
            }

            // 리프레시 토큰 추출
            String refreshToken = jwtTokenProvider.resolveToken(request, AuthToken.REFRESH_TOKEN);
            if (refreshToken == null) {
                log.warn("리프레시 토큰이 없음");
                throw new CommonException(ResponseCode.UNAUTHORIZED);
            }

            // Redis에서 리프레시 토큰 검증
            RefreshToken rt = refreshTokenService.findByAccessTokenId(claims.getId());
            if (rt == null) {
                log.warn("Redis에 리프레시 토큰이 없음");
                throw new CommonException(ResponseCode.UNAUTHORIZED);
            }

            if (!rt.getToken().equals(refreshToken)) {
                log.warn("리프레시 토큰 불일치");
                userBlackListRepository.save(new UserBlackList(claims.getSubject()));
                throw new CommonException(ResponseCode.SECURITY_INCIDENT);
            }

            // 새로운 액세스 토큰 생성
            String username = claims.getSubject();
            String roles = (String) claims.get("roles");
            AccessTokenDto newAccessToken = jwtTokenProvider.generateAccessToken(username, roles);
            
            // 새로운 인증 객체 생성 및 설정
            Authentication authentication = jwtTokenProvider.getAuthentication(newAccessToken.getToken());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 새로운 리프레시 토큰 생성
            RefreshToken newRefreshToken = refreshTokenService.renewingToken(rt.getAtId(), newAccessToken.getJti());

            // 쿠키 설정
            String accessTokenCookie = TokenCookieFactory.create(AuthToken.ACCESS_TOKEN.name(),
                    newAccessToken.getToken(), jwtTokenProvider.getAccessTokenExpiration()).toString();
            String refreshTokenCookie = TokenCookieFactory.create(AuthToken.REFRESH_TOKEN.name(),
                    newRefreshToken.getToken(), newRefreshToken.getTtl() * 1000).toString(); // 초를 밀리초로 변환

            // SameSite=None, Secure=true 설정
            accessTokenCookie += "; SameSite=None; Secure";
            refreshTokenCookie += "; SameSite=None; Secure";

            response.addHeader("Set-Cookie", accessTokenCookie);
            response.addHeader("Set-Cookie", refreshTokenCookie);

            log.info("토큰 갱신 성공: {}", username);

            TokenRefreshResponse refreshResponse = new TokenRefreshResponse(
                    newAccessToken.getToken(),
                    newRefreshToken.getToken(),
                    "Bearer",
                    jwtTokenProvider.getAccessTokenExpiration() / 1000,
                    newRefreshToken.getTtl()
            );

            return ResponseEntity.ok(ApiResponse.success(refreshResponse));

        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            log.error("토큰 갱신 중 오류 발생", e);
            throw new CommonException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 토큰 갱신 응답 DTO
    public static class TokenRefreshResponse {
        private final String accessToken;
        private final String refreshToken;
        private final String grantType;
        private final Long expiresIn;
        private final Long refreshExpiresIn;

        public TokenRefreshResponse(String accessToken, String refreshToken, String grantType, 
                                  Long expiresIn, Long refreshExpiresIn) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.grantType = grantType;
            this.expiresIn = expiresIn;
            this.refreshExpiresIn = refreshExpiresIn;
        }

        // Getters
        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public String getGrantType() { return grantType; }
        public Long getExpiresIn() { return expiresIn; }
        public Long getRefreshExpiresIn() { return refreshExpiresIn; }
    }
} 