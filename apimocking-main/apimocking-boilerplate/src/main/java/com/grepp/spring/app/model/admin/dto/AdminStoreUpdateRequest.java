package com.grepp.spring.app.model.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "장소 수정 용청 DTO")
public record AdminStoreUpdateRequest(

    @Schema(description = "가게 이름", example = "대박 대패삼겹&우삼겹")
    String name,

    @Schema(description = "가게 주소", example = "서울특별시 서울 송파구 천호대로152길 11-1")
    String address,

    @Schema(description = "가게 카테고리", example = "한식")
    String category,

    @Schema(description = "메뉴 목록")
    List<AdminStoreResponse> menus

) {

}
