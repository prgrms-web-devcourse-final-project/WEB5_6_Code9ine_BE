package com.grepp.spring.app.model.member.dto;

// 목표 설정 요청 DTO
// 입력: goalAmount
public class MemberGoalRequest {
    private int goalAmount;

    public MemberGoalRequest() {}
    public MemberGoalRequest(int goalAmount) {
        this.goalAmount = goalAmount;
    }
    public int getGoalAmount() { return goalAmount; }
    public void setGoalAmount(int goalAmount) { this.goalAmount = goalAmount; }
} 