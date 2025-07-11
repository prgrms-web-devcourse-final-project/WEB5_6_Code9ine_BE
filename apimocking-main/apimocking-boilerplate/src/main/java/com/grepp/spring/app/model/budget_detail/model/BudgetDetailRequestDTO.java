package com.grepp.spring.app.model.budget_detail.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetDetailRequestDTO {

    @Schema(description = "수입 또는 지출", example = "수입")
    private String type;

    @Schema(description = "날짜", example = "2025-07-13")
    private String date;

    @Schema(description = "카테고리", example = "식비")
    private String category;

    @Schema(description = "금액", example = "2000")
    private int price;

    @Schema(description = "내용", example = "라면")
    private String content;

    @Schema(description = "반복여부", example = "NONE")
    private String repeatCycle;
}
