package com.grepp.spring.app.model.budget.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
public class AverageSavingResponse {

    private BigDecimal averageSaving;

}
