package com.grepp.spring.app.model.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MemberLoginRequest {
    @Schema(description = "이메일", example = "test3@test.com")
    private String email;
    
    @Schema(description = "비밀번호", example = "string")
    private String password;
} 