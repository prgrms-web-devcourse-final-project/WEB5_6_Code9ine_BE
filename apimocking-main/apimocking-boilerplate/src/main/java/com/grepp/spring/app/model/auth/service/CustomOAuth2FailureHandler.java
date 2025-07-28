package com.grepp.spring.app.model.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@Slf4j
public class CustomOAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                      AuthenticationException exception) throws IOException {
        
        log.error("OAuth2 로그인 실패: {}", exception.getMessage());
        
        // 프론트엔드로 에러와 함께 리다이렉트 (URL 인코딩 적용)
        String targetUrl = UriComponentsBuilder.fromUriString("https://titae.cedartodo.uk/login")
                .queryParam("error", "oauth_failed")
                .queryParam("message", exception.getMessage())
                .build()
                .encode()
                .toUriString();
        
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
} 