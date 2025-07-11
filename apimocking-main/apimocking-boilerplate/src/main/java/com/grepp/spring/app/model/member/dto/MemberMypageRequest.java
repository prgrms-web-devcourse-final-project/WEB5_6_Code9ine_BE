package com.grepp.spring.app.model.member.dto;

import java.util.List;

// 마이페이지 수정 요청 DTO
// 입력: name, profileImage, goalAmount, level, currentExp, nextLevelExp, expProgress, myPosts
public class MemberMypageRequest {
    private String name;
    private String profileImage;
    private int goalAmount;
    private int level;
    private int currentExp;
    private int nextLevelExp;
    private int expProgress;
    private List<MyPostDto> myPosts;

    // MyPostDto 내부 클래스 정의
    public static class MyPostDto {
        private Long postId;
        private String title;
        public MyPostDto() {}
        public MyPostDto(Long postId, String title) {
            this.postId = postId;
            this.title = title;
        }
        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }

    public MemberMypageRequest() {}
    public MemberMypageRequest(String name, String profileImage, int goalAmount, int level, int currentExp, int nextLevelExp, int expProgress, List<MyPostDto> myPosts) {
        this.name = name;
        this.profileImage = profileImage;
        this.goalAmount = goalAmount;
        this.level = level;
        this.currentExp = currentExp;
        this.nextLevelExp = nextLevelExp;
        this.expProgress = expProgress;
        this.myPosts = myPosts;
    }
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
    public List<MyPostDto> getMyPosts() { return myPosts; }
    public void setMyPosts(List<MyPostDto> myPosts) { this.myPosts = myPosts; }
} 