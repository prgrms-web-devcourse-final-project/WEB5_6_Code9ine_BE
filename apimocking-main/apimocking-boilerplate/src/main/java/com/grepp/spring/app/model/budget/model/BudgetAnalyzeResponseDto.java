package com.grepp.spring.app.model.budget.model;

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

    private String yearMonth;
    private int totalIncome;
    private int totalExpense;
    private BudgetGoal goal;
    private int currentMonthExpense;
    private List<BudgetMonthlyExpense> monthlyExpenses;
    private List<BudgetCategorySummary> categorySummary;
    private int savedComparedToLastMonth;
    private int totalsavedAmount;

}
