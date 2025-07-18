package com.grepp.spring.app.model.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "커뮤니티 인기글 응답 DTO")
public record CommunityTopPostResponse(

    @Schema(description = "게시글 고유식별번호", example = "0")
    Long postId,

    @Schema(description = "작성자 닉네임", example = "닉네임")
    String writerNickname,

    @Schema(description = "작성자 칭호", example = "칭호")
    String writerTitle,

    @Schema(description = "작성자 레벨", example = "3")
    int writerLevel,

    @Schema(description = "작성자 프로필 이미지", example = "profile.jpg")
    String writerProfileImage,

    @Schema(description = "게시글 제목", example = "게시물 제목")
    String title,

    @Schema(description = "게시글 작성일", example = "2025-07-03T14:20:00")
    String createdAt,

    @Schema(description = "게시글 카테고리", example = "FREE")
    String category,

    @Schema(description = "좋아요 수", example = "55")
    int likeCount
) {

}