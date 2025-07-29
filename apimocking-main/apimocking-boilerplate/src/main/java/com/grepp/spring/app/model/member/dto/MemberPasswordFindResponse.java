package com.grepp.spring.app.model.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class MemberPasswordFindResponse {
    private String message = "임시 비밀번호가 이메일로 발송되었습니다.";
} 