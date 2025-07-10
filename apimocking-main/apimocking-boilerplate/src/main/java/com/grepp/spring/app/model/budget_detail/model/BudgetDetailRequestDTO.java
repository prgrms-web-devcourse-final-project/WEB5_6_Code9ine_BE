package com.grepp.spring.app.model.budget_detail.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetDetailRequestDTO {

    private String type; // "수입" 또는 "지출"
    private LocalDate date; // "yyyy-MM-dd"
    private String category;
    private BigDecimal amount;
    private String content;
    private String repeatCycle; // "NONE", "DAILY", "WEEKLY" 등
}
