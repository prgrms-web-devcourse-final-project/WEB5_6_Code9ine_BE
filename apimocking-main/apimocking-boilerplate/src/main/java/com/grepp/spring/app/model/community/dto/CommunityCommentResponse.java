package com.grepp.spring.app.model.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "커뮤니티 댓글 응답 DTO")
public record CommunityCommentResponse(
    @Schema(description = "댓글 고유식별번호", example = "0")
    int commentId,

    @Schema(description = "댓글 내용", example = "절약 멋져요!")
    String content,

    @Schema(description = "작성자 닉네임", example = "절약왕")
    String userNickname,

    @Schema(description = "작성자 프로필 이미지", example = "profile1.jpg")
    String userProfileImage,

    @Schema(description = "댓글 작성일시", example = "2025-07-03T15:00:00")
    String createdAt,

    @Schema(description = "댓글 수정일시", example = "2025-07-03T15:30:00")
    String modifiedAt
) {

}
