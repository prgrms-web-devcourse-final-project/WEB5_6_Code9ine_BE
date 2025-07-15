package com.grepp.spring.app.model.budget_detail.model;

import com.grepp.spring.app.model.budget_detail.domain.BudgetDetail;
import java.math.BigDecimal;
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
    private String content;
    private LocalDate date;
    private BigDecimal price;
    private String repeatCycle;

    public static BudgetDetailDto from(BudgetDetail detail) {
        return new BudgetDetailDto(
            detail.getBudgetDetailId(),
            detail.getCategory(),
            detail.getType(),
            detail.getContent(),
            detail.getDate(),
            detail.getPrice(),
            detail.getRepeatCycle()
        );
    }

}
