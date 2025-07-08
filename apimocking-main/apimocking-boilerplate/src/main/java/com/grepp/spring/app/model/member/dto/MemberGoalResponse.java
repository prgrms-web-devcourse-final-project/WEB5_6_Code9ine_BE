package com.grepp.spring.app.model.member.dto;

// 목표 설정 응답 DTO
// 출력: code, message, data(goalAmount)
public class MemberGoalResponse {
    private int code;
    private String message;
    private Data data;

    public static class Data {
        private int goalAmount;
        public Data() {}
        public Data(int goalAmount) { this.goalAmount = goalAmount; }
        public int getGoalAmount() { return goalAmount; }
        public void setGoalAmount(int goalAmount) { this.goalAmount = goalAmount; }
    }

    public MemberGoalResponse() {}
    public MemberGoalResponse(int code, String message, Data data) {
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
} 