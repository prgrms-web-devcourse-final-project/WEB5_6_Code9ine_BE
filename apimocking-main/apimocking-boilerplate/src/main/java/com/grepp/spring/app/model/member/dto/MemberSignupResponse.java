package com.grepp.spring.app.model.member.dto;

// 회원가입 응답 DTO
// 출력: code, message, data(userId)
public class MemberSignupResponse {
    private int code;
    private String message;
    private Data data;

    public MemberSignupResponse() {}
    public MemberSignupResponse(int code, String message, Data data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }

    public static class Data {
        private Long userId;
        public Data() {}
        public Data(Long userId) { this.userId = userId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }
} 