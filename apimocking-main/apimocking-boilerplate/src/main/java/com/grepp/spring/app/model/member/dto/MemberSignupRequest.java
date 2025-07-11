package com.grepp.spring.app.model.member.dto;

// 회원가입 요청 DTO
// 입력: email, password, name
public class MemberSignupRequest {
    private String email;
    private String password;
    private String name;

    public MemberSignupRequest() {}
    public MemberSignupRequest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
} 