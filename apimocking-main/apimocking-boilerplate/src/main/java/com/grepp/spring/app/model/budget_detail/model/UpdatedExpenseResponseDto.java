package com.grepp.spring.app.model.budget_detail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedExpenseResponseDto {

    private Long id;
    private String type;
    private String date;
    private String category;
    private int amount;
    private String content;
    private String repeatCycle;

}
