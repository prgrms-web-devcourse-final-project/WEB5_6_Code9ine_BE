package com.grepp.spring.app.model.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EmailVerificationVerifyRequest {
    private String email;
    private String code;
} 