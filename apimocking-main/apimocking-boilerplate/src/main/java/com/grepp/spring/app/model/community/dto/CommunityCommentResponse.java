package com.grepp.spring.app.model.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "커뮤니티 댓글 응답 DTO")
public record CommunityCommentResponse(
    @Schema(description = "댓글 고유식별번호", example = "0")
    int commentId,

    @Schema(description = "댓글 내용", example = "댓글 내용")
    String content,

    @Schema(description = "작성자 닉네임", example = "닉네임")
    String writerNickname,

    @Schema(description = "작성자 프로필 이미지", example = "profile.jpg")
    String writerProfileImage,

    @Schema(description = "작성자 칭호", example = "칭호")
    String writerTitle,

    @Schema(description = "작성자 휘장", example = "휘장")
    String writerSymbol,

    @Schema(description = "댓글 작성일시", example = "2025-07-03T15:00:00")
    String createdAt,

    @Schema(description = "댓글 수정일시", example = "2025-07-03T15:30:00")
    String modifiedAt
) {

}
