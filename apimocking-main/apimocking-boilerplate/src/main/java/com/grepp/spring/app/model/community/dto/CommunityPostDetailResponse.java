package com.grepp.spring.app.model.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Schema(description = "커뮤니티 게시글 생성 응답 DTO")
public record CommunityPostDetailResponse(
    @Schema(description = "게시글 고유식별번호", example = "0")
    Long postId,

    @Schema(description = "유저 고유식별번호", example = "0")
    Long memberId,

    @Schema(
        description = "게시글 카테고리",
        example = "CHALLENGE",
        allowableValues = {"MY_STORE", "CHALLENGE", "FREE"}
    )
    @NotBlank(message = "게시물 카테고리는 필수입니다.")
    String category,

    @Schema(
        description = "챌린지 세부 카테고리 (CHALLENGE일 경우에만)",
        example = "NO_MONEY",
        allowableValues = {"NO_MONEY", "KIND_CONSUMER", "DETECTIVE", "MASTER", "COOK_KING"}
    )
    @NotBlank(message = "챌린지 카테고리는 필수입니다.")
    String challengeCategory,

    @Schema(description = "게시글 제목", example = "게시물 제목")
    String title,

    @Schema(description = "게시글 작성일시", example = "2025-07-03T14:20:00")
    String createdAt,

    @Schema(description = "게시글 내용", example = "게시물 내용")
    String content,

    @Schema(description = "이미지 파일 목록", example = "[\"image1.jpg\", \"image2.jpg\"]")
    List<String> imageUrls,

    @Schema(description = "댓글 수", example = "4")
    int commentCount,

    @Schema(description = "좋아요 수", example = "55")
    int likeCount,

    @Schema(description = "좋아요 여부", example = "true")
    boolean isLiked,

    @Schema(description = "북마크 여부", example = "true")
    boolean isBookmarked,

    @Schema(description = "챌린지 달성 여부", example = "false")
    boolean challengeAchieved,

    @Schema(description = "작성자 닉네임", example = "닉네임")
    String writerNickname,

    @Schema(description = "작성자 칭호", example = "칭호")
    String writerTitle,

    @Schema(description = "작성자 휘장", example = "3")
    int writerLevel,

    @Schema(description = "작성자 프로필 이미지", example = "profile.jpg")
    String writerProfileImage
) {

}