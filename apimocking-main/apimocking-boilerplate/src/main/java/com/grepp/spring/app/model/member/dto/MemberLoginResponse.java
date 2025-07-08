package com.grepp.spring.app.model.member.dto;

// 멤버 로그인 응답 DTO
// 출력: code, message, data(accessToken, refreshToken)
public class MemberLoginResponse {
    private int code;
    private String message;
    private Data data;

    public MemberLoginResponse() {}
    public MemberLoginResponse(int code, String message, Data data) {
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
        private String accessToken;
        private String refreshToken;
        public Data() {}
        public Data(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }
} 