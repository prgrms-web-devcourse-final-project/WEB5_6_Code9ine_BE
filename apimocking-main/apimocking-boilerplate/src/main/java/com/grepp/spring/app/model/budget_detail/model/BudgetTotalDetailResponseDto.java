package com.grepp.spring.app.model.budget_detail.model;

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
public class BudgetTotalDetailResponseDto {

    private String yearMonth;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal totalDifference;
    private List<BudgetDetailDto> details;

}
