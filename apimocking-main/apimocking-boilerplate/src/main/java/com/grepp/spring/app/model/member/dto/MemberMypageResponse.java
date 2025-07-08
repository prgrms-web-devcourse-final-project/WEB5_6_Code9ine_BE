package com.grepp.spring.app.model.member.dto;

import java.util.List;

// 마이페이지 조회 응답 DTO
// 출력: code, message, data(마이페이지 정보)
public class MemberMypageResponse {
    private int code;
    private String message;
    private Data data;

    public static class Data {
        private String email;
        private String name;
        private String profileImage;
        private int goalAmount;
        private int level;
        private int currentExp;
        private int nextLevelExp;
        private int expProgress;
        private List<MemberMypageRequest.MyPostDto> myPosts;
        public Data() {}
        public Data(String email, String name, String profileImage, int goalAmount, int level, int currentExp, int nextLevelExp, int expProgress, List<MemberMypageRequest.MyPostDto> myPosts) {
            this.email = email;
            this.name = name;
            this.profileImage = profileImage;
            this.goalAmount = goalAmount;
            this.level = level;
            this.currentExp = currentExp;
            this.nextLevelExp = nextLevelExp;
            this.expProgress = expProgress;
            this.myPosts = myPosts;
        }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getProfileImage() { return profileImage; }
        public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
        public int getGoalAmount() { return goalAmount; }
        public void setGoalAmount(int goalAmount) { this.goalAmount = goalAmount; }
        public int getLevel() { return level; }
        public void setLevel(int level) { this.level = level; }
        public int getCurrentExp() { return currentExp; }
        public void setCurrentExp(int currentExp) { this.currentExp = currentExp; }
        public int getNextLevelExp() { return nextLevelExp; }
        public void setNextLevelExp(int nextLevelExp) { this.nextLevelExp = nextLevelExp; }
        public int getExpProgress() { return expProgress; }
        public void setExpProgress(int expProgress) { this.expProgress = expProgress; }
        public List<MemberMypageRequest.MyPostDto> getMyPosts() { return myPosts; }
        public void setMyPosts(List<MemberMypageRequest.MyPostDto> myPosts) { this.myPosts = myPosts; }
    }

    public MemberMypageResponse() {}
    public MemberMypageResponse(int code, String message, Data data) {
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