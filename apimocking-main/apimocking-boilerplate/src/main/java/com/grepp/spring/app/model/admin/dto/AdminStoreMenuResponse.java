package com.grepp.spring.app.model.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "관리자 가게 메뉴 응답 DTO")
public record AdminStoreMenuResponse(

    @Schema(description = "메뉴 이름", example = "대패삼겹살")
    String name,

    @Schema(description = "메뉴 가격", example = "4500")
    BigDecimal price

    ) {

}
