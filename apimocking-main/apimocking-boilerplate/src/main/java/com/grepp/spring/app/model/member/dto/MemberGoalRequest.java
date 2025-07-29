package com.grepp.spring.app.model.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MemberGoalRequest {
    @Schema(description = "목표 금액", example = "200000")
    private BigDecimal goalAmount;
    
    @Schema(description = "목표 항목", example = "자동차")
    private String goalStuff;
} 