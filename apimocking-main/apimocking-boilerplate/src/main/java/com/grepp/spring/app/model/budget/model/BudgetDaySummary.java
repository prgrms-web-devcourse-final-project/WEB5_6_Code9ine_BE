package com.grepp.spring.app.model.budget.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BudgetDaySummary {

    private String date;
    private int income;
    private int expense;
    private int difference;

}
