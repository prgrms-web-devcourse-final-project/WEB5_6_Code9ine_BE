package com.grepp.spring.app.model.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Schema(description = "관리자 가게 등록 요청 DTO")
public record AdminStoreCreateRequest(

    @Schema(description = "가게 이름", example = "전통평양냉면 제형면옥 하계점")
    @NotBlank(message = "가게 이름은 필수입니다.")
    String name,

    @Schema(description = "가게 주소", example = "서울 노원구 공릉로59나길 78-10 제형면옥")
    @NotBlank(message = "가게 주소는 필수입니다.")
    String address,

    @Schema(description = "가게 카테고리", example = "한식")
    @NotBlank(message = "카테고리는 필수입니다.")
    String category,

    @Schema(description = "메뉴 목록")
    List<@Valid AdminStoreMenuCreateRequest> menus

    ) {

}
