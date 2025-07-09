package com.grepp.spring.app.model.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자 당일 통계(방문자, 회원가입 수) 조회 응답 DTO")
public record AdminStatsResponse(

    @Schema(description = "날짜", example = "2025-07-03")
    String date,

    @Schema(description = "방문자 수", example = "100")
    int visitorCount,

    @Schema(description = "회원가입 수", example = "10")
    int signupCount

) {

}
