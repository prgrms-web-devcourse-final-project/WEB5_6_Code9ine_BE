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
public class UpdatedBudgetDetailResponseDto {

    private Long id;
    private String type;
    private LocalDate date;
    private String category;
    private int price;
    private String content;
    private String repeatCycle;

}
