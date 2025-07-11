package com.grepp.spring.app.model.budget_detail.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BudgetDetailDto {

    private Long id;
    private String category;
    private String type;
    private String categoryIcon;
    private String content;
    private LocalDate date;
    private int price;
    private String repeatCycle;

}
