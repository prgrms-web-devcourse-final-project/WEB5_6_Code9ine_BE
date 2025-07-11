package com.grepp.spring.app.model.budget_detail.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDetailResponseDto {

    private List<BudgetDetailDto> details;

}
