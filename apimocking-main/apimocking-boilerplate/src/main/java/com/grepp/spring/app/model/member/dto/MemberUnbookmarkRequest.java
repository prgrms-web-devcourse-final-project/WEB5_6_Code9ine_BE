package com.grepp.spring.app.model.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MemberUnbookmarkRequest {
    @Schema(description = "장소 타입", example = "store|festival|library")
    private String placeType;
} 