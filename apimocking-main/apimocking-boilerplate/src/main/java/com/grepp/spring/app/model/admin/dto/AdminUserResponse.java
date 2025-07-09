package com.grepp.spring.app.model.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자 유저 응답 DTO")
public record AdminUserResponse(

    @Schema(description = "유저 고유식별번호", example = "0")
    int userId,

    @Schema(description = "닉네임", example = "거지왕")
    String nickname,

    @Schema(description = "이메일", example = "abc@abc.com")
    String email,

    @Schema(description = "차단여부", example = "false")
    boolean activated
) {

}
