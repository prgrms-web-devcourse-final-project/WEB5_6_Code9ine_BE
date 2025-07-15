package com.grepp.spring.app.model.budget.model;

import com.google.type.Decimal;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetMonthlyExpense {

    private String month;
    private BigDecimal amount;

}
