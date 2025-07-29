package com.grepp.spring.app.model.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MemberProfileUpdateRequest {
    @Schema(description = "닉네임", example = "새로운닉네임")
    private String nickname;
    
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/image.jpg")
    private String profileImage;
    
    @Schema(description = "기존 비밀번호", example = "1234")
    private String oldPassword; // 기존 비밀번호(비밀번호 변경 시 필요)
    
    @Schema(description = "새 비밀번호", example = "newpassword123!")
    private String newPassword;
    
    @Schema(description = "새 비밀번호 확인", example = "newpassword123!")
    private String newPasswordCheck;
} 