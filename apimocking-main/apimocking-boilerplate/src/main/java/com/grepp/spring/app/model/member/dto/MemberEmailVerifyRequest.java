package com.grepp.spring.app.model.member.dto;

// 이메일 인증(코드 검증) 요청 DTO
// 입력: email, code
public class MemberEmailVerifyRequest {
    private String email;
    private String code;

    public MemberEmailVerifyRequest() {}
    public MemberEmailVerifyRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
} 