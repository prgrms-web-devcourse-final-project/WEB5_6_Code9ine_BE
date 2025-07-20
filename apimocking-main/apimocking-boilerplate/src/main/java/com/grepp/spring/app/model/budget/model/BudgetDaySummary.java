package com.grepp.spring.app.model.budget.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudgetDaySummary {

    private LocalDate date;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal difference;

    public BudgetDaySummary(LocalDate date, BigDecimal income, BigDecimal expense) {
        this.date = date;
        this.income = income != null ? income : BigDecimal.ZERO;
        this.expense = expense != null ? expense : BigDecimal.ZERO;
        this.difference = this.income.subtract(this.expense);
    }
}
