package com.grepp.spring.app.model.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "커뮤니티 인기글 응답 DTO")
public record CommunityTopPostResponse(

    @Schema(description = "게시글 고유식별번호", example = "0")
    int postId,

    @Schema(description = "작성자 닉네임", example = "거지왕")
    String writerNickname,

    @Schema(description = "작성자 칭호", example = "절약왕")
    String writerTitle,

    @Schema(description = "게시글 제목", example = "텀블러로 절약 성공!")
    String title,

    @Schema(description = "게시글 작성일", example = "2025-07-03")
    String createdAt
) {

}