package com.grepp.spring.app.controller.api.member;

import com.grepp.spring.app.model.auth.code.AuthToken;
import com.grepp.spring.infra.auth.jwt.TokenCookieFactory;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TokenRefreshController {

    // OAuth2 로그인 후 토큰을 HttpOnly 쿠키로 설정하는 엔드포인트
    @PostMapping("/set-tokens")
    public ResponseEntity<ApiResponse<String>> setTokensFromOAuth2(
            @RequestBody TokenSetRequest request,
            HttpServletResponse response) {
        
        log.info("OAuth2 토큰 설정 요청: expiresIn={}, refreshExpiresIn={}", 
                request.getExpiresIn(), request.getRefreshExpiresIn());
        
        try {
            // Access Token HttpOnly 쿠키 설정
            var accessTokenCookie = TokenCookieFactory.create(
                AuthToken.ACCESS_TOKEN.name(),
                request.getAccessToken(),
                request.getExpiresIn() * 1000 // 초를 밀리초로 변환
            );
            
            // Refresh Token HttpOnly 쿠키 설정
            var refreshTokenCookie = TokenCookieFactory.create(
                AuthToken.REFRESH_TOKEN.name(),
                request.getRefreshToken(),
                request.getRefreshExpiresIn() * 1000 // 초를 밀리초로 변환
            );
            
            // Set-Cookie 헤더에 HttpOnly 쿠키 추가
            response.addHeader("Set-Cookie", accessTokenCookie.toString());
            response.addHeader("Set-Cookie", refreshTokenCookie.toString());
            
            log.info("OAuth2 토큰이 HttpOnly 쿠키에 설정되었습니다. AccessToken 길이: {}", 
                    request.getAccessToken().length());
            
            return ResponseEntity.ok(ApiResponse.success("토큰이 성공적으로 설정되었습니다."));
            
        } catch (Exception e) {
            log.error("OAuth2 토큰 설정 중 오류 발생", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "토큰 설정에 실패했습니다."));
        }
    }

    // 토큰 설정 요청 DTO
    public static class TokenSetRequest {
        private String accessToken;
        private String refreshToken;
        private Long expiresIn;
        private Long refreshExpiresIn;
        private String role;
        
        // Getters and Setters
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
        
        public Long getExpiresIn() { return expiresIn; }
        public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
        
        public Long getRefreshExpiresIn() { return refreshExpiresIn; }
        public void setRefreshExpiresIn(Long refreshExpiresIn) { this.refreshExpiresIn = refreshExpiresIn; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
} 