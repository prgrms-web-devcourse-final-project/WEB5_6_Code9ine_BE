package com.grepp.spring.app.model.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "커뮤니티 게시글 수정 요청 DTO")
public record CommunityPostUpdateRequest(

    @Schema(description = "게시글 제목", example = "게시글 제목")
    String title,

    @Schema(description = "게시글 내용", example = "게시글 내용")
    String content,

    @Schema(description = "이미지 파일 목록", example = "[\"image1.jpg\", \"image2.jpg\"]")
    List<String> imageUrls,

    @Schema(description = "게시글 카테고리", example = "나가게")
    String category

) {

}