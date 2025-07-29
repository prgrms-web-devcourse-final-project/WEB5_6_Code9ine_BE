package com.grepp.spring.app.model.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MemberPasswordChangeRequest {
    @Schema(description = "기존 비밀번호", example = "1234")
    @NotBlank(message = "기존 비밀번호는 필수입니다.")
    private String oldPassword;
    
    @Schema(description = "새 비밀번호", example = "5678")
    @NotBlank(message = "새 비밀번호는 필수입니다.")
    private String newPassword;
} 