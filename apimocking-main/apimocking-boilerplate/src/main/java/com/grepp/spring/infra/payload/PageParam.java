package com.grepp.spring.infra.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "페이지네이션 요청")
public class PageParam{

    @Schema(description = "페이지 번호 (1부터 시작)", example = "1", defaultValue = "1")
    @Min(1)
    private int page = 1;

    @Schema(description = "페이지당 데이터 수", example = "10", defaultValue = "10")
    @Min(1)
    private int size = 10;
}
