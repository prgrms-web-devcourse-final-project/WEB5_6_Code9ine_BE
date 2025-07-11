package com.grepp.spring.app.model.member.dto;

// 비밀번호 변경 요청 DTO
// 입력: email, newPassword
public class MemberPasswordResetRequest {
    private String email;
    private String newPassword;

    public MemberPasswordResetRequest() {}
    public MemberPasswordResetRequest(String email, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
} 