package com.grepp.spring.app.model.member.dto;

// 프로필 이미지 변경 요청 DTO
// 입력: imageUrl
public class MemberProfileImageRequest {
    private String imageUrl;

    public MemberProfileImageRequest() {}
    public MemberProfileImageRequest(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
} 