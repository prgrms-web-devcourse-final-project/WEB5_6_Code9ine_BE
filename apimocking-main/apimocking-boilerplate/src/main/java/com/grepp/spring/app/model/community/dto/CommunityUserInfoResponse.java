package com.grepp.spring.app.model.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "커뮤니티 유저 정보 응답 DTO")
public record CommunityUserInfoResponse(

    @Schema(description = "유저 닉네임", example = "거지왕")
    String userNickname,

    @Schema(description = "유저 프로필 이미지 URL", example = "image1.jpg")
    String userImgUrl,

    @Schema(description = "유저 타이틀", example = "개근왕")
    String userTitle,

    @Schema(description = "유저 심볼", example = "누더기")
    String userSymbol

) {

}