package com.grepp.spring.app.model.budget.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetCategorySummary {

    private String category;
    private BigDecimal totalAmount;
}
