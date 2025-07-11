package com.grepp.spring.app.model.member.dto;

// 비밀번호 찾기 요청 DTO
// 입력: email
public class MemberPasswordFindRequest {
    private String email;

    public MemberPasswordFindRequest() {}
    public MemberPasswordFindRequest(String email) {
        this.email = email;
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
} 