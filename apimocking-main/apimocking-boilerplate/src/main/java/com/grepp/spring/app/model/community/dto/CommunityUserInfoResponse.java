package com.grepp.spring.app.model.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "커뮤니티 유저 정보 응답 DTO")
public record CommunityUserInfoResponse(

    @Schema(description = "유저 고유식별번호", example = "0")
    Long memberId,

    @Schema(description = "유저 닉네임", example = "닉네임")
    String userNickname,

    @Schema(description = "유저 프로필 이미지 URL", example = "image1.jpg")
    String userProfileImg,

    @Schema(description = "유저 칭호", example = "칭호")
    String userTitle,

    @Schema(description = "유저 레벨", example = "레벨")
    int userLevel

) {

}