package com.grepp.spring.app.model.budget_detail.service;

import com.grepp.spring.app.model.budget.domain.Budget;
import com.grepp.spring.app.model.budget.repos.BudgetRepository;
import com.grepp.spring.app.model.budget_detail.domain.BudgetDetail;
import com.grepp.spring.app.model.budget_detail.model.BudgetDetailDTO;
import com.grepp.spring.app.model.budget_detail.repos.BudgetDetailRepository;
import com.grepp.spring.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class BudgetDetailService {

    private final BudgetDetailRepository budgetDetailRepository;
    private final BudgetRepository budgetRepository;

    public BudgetDetailService(final BudgetDetailRepository budgetDetailRepository,
            final BudgetRepository budgetRepository) {
        this.budgetDetailRepository = budgetDetailRepository;
        this.budgetRepository = budgetRepository;
    }

    public List<BudgetDetailDTO> findAll() {
        final List<BudgetDetail> budgetDetails = budgetDetailRepository.findAll(Sort.by("budgetDetailId"));
        return budgetDetails.stream()
                .map(budgetDetail -> mapToDTO(budgetDetail, new BudgetDetailDTO()))
                .toList();
    }

    public BudgetDetailDTO get(final Long budgetDetailId) {
        return budgetDetailRepository.findById(budgetDetailId)
                .map(budgetDetail -> mapToDTO(budgetDetail, new BudgetDetailDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final BudgetDetailDTO budgetDetailDTO) {
        final BudgetDetail budgetDetail = new BudgetDetail();
        mapToEntity(budgetDetailDTO, budgetDetail);
        return budgetDetailRepository.save(budgetDetail).getBudgetDetailId();
    }

    public void update(final Long budgetDetailId, final BudgetDetailDTO budgetDetailDTO) {
        final BudgetDetail budgetDetail = budgetDetailRepository.findById(budgetDetailId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(budgetDetailDTO, budgetDetail);
        budgetDetailRepository.save(budgetDetail);
    }

    public void delete(final Long budgetDetailId) {
        budgetDetailRepository.deleteById(budgetDetailId);
    }

    private BudgetDetailDTO mapToDTO(final BudgetDetail budgetDetail,
            final BudgetDetailDTO budgetDetailDTO) {
        budgetDetailDTO.setBudgetDetailId(budgetDetail.getBudgetDetailId());
        budgetDetailDTO.setContent(budgetDetail.getContent());
        budgetDetailDTO.setPrice(budgetDetail.getPrice());
        budgetDetailDTO.setCategory(budgetDetail.getCategory());
        budgetDetailDTO.setDate(budgetDetail.getDate());
        budgetDetailDTO.setType(budgetDetail.getType());
        budgetDetailDTO.setBudget(budgetDetail.getBudget() == null ? null : budgetDetail.getBudget().getBudgetId());
        return budgetDetailDTO;
    }

    private BudgetDetail mapToEntity(final BudgetDetailDTO budgetDetailDTO,
            final BudgetDetail budgetDetail) {
        budgetDetail.setContent(budgetDetailDTO.getContent());
        budgetDetail.setPrice(budgetDetailDTO.getPrice());
        budgetDetail.setCategory(budgetDetailDTO.getCategory());
        budgetDetail.setDate(budgetDetailDTO.getDate());
        budgetDetail.setType(budgetDetailDTO.getType());
        final Budget budget = budgetDetailDTO.getBudget() == null ? null : budgetRepository.findById(budgetDetailDTO.getBudget())
                .orElseThrow(() -> new NotFoundException("budget not found"));
        budgetDetail.setBudget(budget);
        return budgetDetail;
    }

}
