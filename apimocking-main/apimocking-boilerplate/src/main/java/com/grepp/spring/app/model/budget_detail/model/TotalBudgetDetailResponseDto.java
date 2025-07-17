package com.grepp.spring.app.model.budget_detail.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TotalBudgetDetailResponseDto {

    private List<BudgetDetailDto> details;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

}
