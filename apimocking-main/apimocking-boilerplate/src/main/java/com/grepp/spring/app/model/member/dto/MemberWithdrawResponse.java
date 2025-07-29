package com.grepp.spring.app.model.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MemberWithdrawResponse {
    @Schema(description = "회원 탈퇴 성공 메시지", example = "회원 탈퇴가 완료되었습니다.")
    private String message = "회원 탈퇴가 완료되었습니다.";
} 