package com.grepp.spring.app.model.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "커뮤니티 게시글 생성 응답 DTO")
public record CommunityPostDetailResponse(
    @Schema(description = "게시글 고유식별번호", example = "0")
    int postId,

    @Schema(description = "게시글 카테고리", example = "나가게")
    String category,

    @Schema(description = "게시글 제목", example = "오늘도 커피 대신 물!")
    String title,

    @Schema(description = "게시글 작성일시", example = "2025-07-03T14:20:00")
    String createdAt,

    @Schema(description = "게시글 내용", example = "카페 대신 집에서 커피 내려 마셨어요. 하루 4천 원 절약!")
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

    @Schema(description = "작성자 닉네임", example = "거지왕")
    String writerNickname,

    @Schema(description = "작성자 칭호", example = "거지왕")
    String writerTitle,

    @Schema(description = "작성자 프로필 이미지", example = "profile1.jpg")
    String writerProfileImage
) {

}