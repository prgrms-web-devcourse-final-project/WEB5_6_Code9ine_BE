package com.grepp.spring.app.model.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자 가게 메뉴 등록 요청 DTO")
public record AdminStoreMenuCreateRequest (

    @Schema(description = "메뉴 이름", example = "평양물냉면")
    String name,

    @Schema(description = "메뉴 가격", example = "14000")
    int price

    ) {

}
