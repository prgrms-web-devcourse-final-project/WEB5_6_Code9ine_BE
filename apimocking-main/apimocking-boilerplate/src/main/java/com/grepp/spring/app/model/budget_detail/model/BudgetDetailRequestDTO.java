package com.grepp.spring.app.model.budget_detail.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetDetailRequestDTO {

    private String type; // "수입" 또는 "지출"
    private String date; // "yyyy-MM-dd"
    private String category;
    private int amount;
    private String content;
    private String repeatCycle; // "NONE", "DAILY", "WEEKLY" 등
}
