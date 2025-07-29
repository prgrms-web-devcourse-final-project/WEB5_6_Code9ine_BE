package com.grepp.spring.app.model.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
public class MemberLogoutResponse {
    private String message = "로그아웃 성공";
    
    public MemberLogoutResponse() {}
    
    public MemberLogoutResponse(String message) { 
        this.message = message; 
    }
} 