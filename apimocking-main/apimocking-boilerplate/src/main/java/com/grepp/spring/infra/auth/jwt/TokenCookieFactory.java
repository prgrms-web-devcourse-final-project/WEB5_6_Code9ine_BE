package com.grepp.spring.infra.auth.jwt;

import org.springframework.http.ResponseCookie;

public class TokenCookieFactory {
    public static ResponseCookie create(String name, String value, Long expires) {
        return ResponseCookie.from(name, value)
                   .maxAge(expires / 1000)     // 밀리초를 초로 변환
                   .path("/")
                   .httpOnly(true)             // HttpOnly
                   .secure(true)               // 운영 환경에서는 true
                   .sameSite("None")           // 크로스 사이트 허용
                   .build();
    }
    
    public static ResponseCookie createExpiredToken(String name) {
        return ResponseCookie.from(name, "")
                   .maxAge(0)
                   .path("/")
                   .httpOnly(true)             // HttpOnly
                   .secure(true)               // 운영 환경에서는 true
                   .sameSite("None")           // 크로스 사이트 허용
                   .build();
    }
}
