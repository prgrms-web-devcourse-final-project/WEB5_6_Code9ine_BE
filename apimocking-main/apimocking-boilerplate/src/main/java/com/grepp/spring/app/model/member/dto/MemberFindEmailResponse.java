package com.grepp.spring.app.model.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MemberFindEmailResponse {
    @Schema(description = "마스킹된 이메일 목록", example = "[\"te****@test.com\", \"an****@test.com\"]")
    private List<String> emails;
} 