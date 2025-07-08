package com.grepp.spring.app.model.budget_detail.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BudgetDetailDTO {

    private Long budgetDetailId;

    @NotNull
    private Long budgetId;

    @NotNull
    @Size(max = 100)
    private String content;

    @NotNull
    @Digits(integer = 20, fraction = 1)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "75.8")
    private BigDecimal price;

    @NotNull
    @Size(max = 255)
    private String category;

    @NotNull
    private LocalDate date;

    @NotNull
    @Size(max = 255)
    private String type;

    @NotNull
    private Long budget;

}
