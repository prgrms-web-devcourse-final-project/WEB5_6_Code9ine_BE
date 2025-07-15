package com.grepp.spring.app.model.budget.model;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BudgetCalenderResponseDto {

    private String month;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private List<BudgetDaySummary> days;

}
