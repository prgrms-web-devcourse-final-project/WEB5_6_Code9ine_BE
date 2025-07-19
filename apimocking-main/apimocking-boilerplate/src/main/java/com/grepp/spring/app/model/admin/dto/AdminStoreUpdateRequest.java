package com.grepp.spring.app.model.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;

@Schema(description = "관리자 장소 수정 용청 DTO")
public record AdminStoreUpdateRequest(

    @Schema(description = "가게 이름", example = "전통평양냉면 제형면옥 하계점")
    String name,

    @Schema(description = "가게 주소", example = "서울 노원구 공릉로59나길 78-10 제형면옥")
    String address,

    @Schema(description = "가게 카테고리", example = "한식")
    String category,

    @Schema(description = "메뉴 목록")
    List<AdminStoreMenuCreateRequest> menus

) {

}
