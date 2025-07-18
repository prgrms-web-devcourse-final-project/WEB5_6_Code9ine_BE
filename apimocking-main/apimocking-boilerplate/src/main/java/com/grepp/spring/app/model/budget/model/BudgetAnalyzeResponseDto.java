package com.grepp.spring.app.model.budget.model;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetAnalyzeResponseDto {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BudgetGoal goal;
    private List<BudgetMonthlyExpense> monthlyExpenses;
    private List<BudgetCategorySummary> categorySummary;
    private BigDecimal totalsavedAmount;

}
