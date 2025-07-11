package com.grepp.spring.app.model.budget.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BudgetDTO {

    private Long budgetId;

    @NotNull
    private Long memberId;

    private LocalDate date;

    @Digits(integer = 20, fraction = 1)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "27.8")
    private int totalIncome;

    @Digits(integer = 20, fraction = 1)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "44.8")
    private int totalExpense;

    @Digits(integer = 20, fraction = 1)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "67.8")
    private int targetExpense;

    @NotNull
    private Long member;

}
