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

}
