package com.grepp.spring.app.model.member.dto;

// 멤버 로그인 요청 DTO
// 입력: email, password
public class MemberLoginRequest {
    private String email;
    private String password;

    public MemberLoginRequest() {}
    public MemberLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
} 