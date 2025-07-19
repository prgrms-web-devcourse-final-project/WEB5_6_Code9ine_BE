package com.grepp.spring.app.model.budget.service;

import com.grepp.spring.app.model.budget.model.AllSavingResponse;
import com.grepp.spring.app.model.budget.model.AverageSavingResponse;
import com.grepp.spring.app.model.budget.repos.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class MainPageBudgetService {

    private final BudgetRepository budgetRepository;

    public AverageSavingResponse getAverageSaving() {
        BigDecimal averageSaving = budgetRepository.getAverageSaving();
        return new AverageSavingResponse(averageSaving);
    }

    public AllSavingResponse getAllSaving() {
        BigDecimal allSaving = budgetRepository.getAllSaving();
        return new AllSavingResponse(allSaving);
    }
}
