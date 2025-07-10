package com.grepp.spring.app.model.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "커뮤니티 댓글 작성 요청 DTO")
public record CommunityCommentCreateRequest(

    @Schema(description = "댓글 내용", example = "댓글 내용")
    @NotBlank(message = "댓글 내용은 필수입니다.")
    String content

) {

}