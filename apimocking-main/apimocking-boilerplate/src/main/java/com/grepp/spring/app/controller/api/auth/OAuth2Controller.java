package com.grepp.spring.app.controller.api.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuth2Controller {

    @GetMapping("/google")
    public RedirectView googleLogin() {
        // Google OAuth2 로그인 시작 - 직접 리다이렉트
        return new RedirectView("/oauth2/authorization/google");
    }
} 