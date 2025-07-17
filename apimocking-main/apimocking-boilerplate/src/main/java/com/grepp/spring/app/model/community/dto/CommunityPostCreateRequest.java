package com.grepp.spring.app.model.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Schema(description = "커뮤니티 게시글 생성 요청 DTO")
public record CommunityPostCreateRequest(

    @Schema(description = "게시글 제목", example = "게시물 제목")
    @NotBlank(message = "게시물 제목은 필수입니다.")
    String title,

    @Schema(description = "게시글 내용", example = "게시물 내용")
    @NotBlank(message = "게시물 내용은 필수입니다.")
    String content,

    @Schema(description = "이미지 파일 목록", example = "[\"image0.jpg\", \"image1.jpg\"]")
    List<String> imageUrls,

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
    String challengeCategory

) {

}