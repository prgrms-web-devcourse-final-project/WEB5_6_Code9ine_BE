package com.grepp.spring.app.model.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Schema(description = "장소 등록 용청 DTO")
public record AdminStoreCreateRequest(

    @Schema(description = "가게 이름", example = "대박 대패삼겹&우삼겹")
    @NotBlank(message = "가게 이름은 필수입니다.")
    String name,

    @Schema(description = "가게 주소", example = "서울특별시 서울 송파구 천호대로152길 11-1")
    @NotBlank(message = "가게 주소는 필수입니다.")
    String address,

    @Schema(description = "가게 카테고리", example = "한식")
    @NotBlank(message = "카테고리는 필수입니다.")
    String category,

    @Schema(description = "메뉴 목록")
    List<AdminStoreMenuResponse> menus

    ) {

}
