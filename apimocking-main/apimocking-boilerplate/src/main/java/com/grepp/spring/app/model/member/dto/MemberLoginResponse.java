package com.grepp.spring.app.model.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MemberLoginResponse {
    private Data data;
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Data {
        private String accessToken;
        private String refreshToken;
        private String grantType;
        private Long expiresIn;
        private Long refreshExpiresIn;
        private String role; // 추가된 필드
    }
} 