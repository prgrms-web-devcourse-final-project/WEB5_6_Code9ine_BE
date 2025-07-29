package com.grepp.spring.app.model.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MemberFindEmailRequest {
    @Schema(description = "이름", example = "안재호")
    @NotBlank(message = "이름은 필수입니다.")
    private String name;
    
    @Schema(description = "휴대폰번호", example = "01012345678")
    @NotBlank(message = "휴대폰번호는 필수입니다.")
    @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "휴대폰번호 형식이 올바르지 않습니다.")
    private String phoneNumber;
} 