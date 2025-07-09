package com.grepp.spring.app.model.budget.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BudgetGoal {

    private String itemName;
    private String itemImage;
    private int itemPrice;

}
