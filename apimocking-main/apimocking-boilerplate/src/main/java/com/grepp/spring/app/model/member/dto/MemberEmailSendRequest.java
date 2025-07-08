package com.grepp.spring.app.model.member.dto;

// 이메일 인증(코드 발송) 요청 DTO
// 입력: email
public class MemberEmailSendRequest {
    private String email;

    public MemberEmailSendRequest() {}
    public MemberEmailSendRequest(String email) {
        this.email = email;
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
} 