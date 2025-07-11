package com.grepp.spring.app.model.member.dto;

// 비밀번호 찾기 응답 DTO
// 출력: code, message, data(null)
public class MemberPasswordFindResponse {
    private int code;
    private String message;
    private Object data;

    public MemberPasswordFindResponse() {}
    public MemberPasswordFindResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
} 