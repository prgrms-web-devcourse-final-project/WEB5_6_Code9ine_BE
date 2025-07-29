package com.grepp.spring.app.model.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OAuth2SignupRequest {
    private String email;
    private String name;
    private String nickname;
    private String profileImage;
} 