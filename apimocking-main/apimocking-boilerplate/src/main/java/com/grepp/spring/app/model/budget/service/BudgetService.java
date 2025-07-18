package com.grepp.spring.app.model.budget.service;

import com.grepp.spring.app.model.budget.domain.Budget;
import com.grepp.spring.app.model.budget.model.BudgetDTO;
import com.grepp.spring.app.model.budget.repos.BudgetRepository;
import com.grepp.spring.app.model.budget_detail.domain.BudgetDetail;
import com.grepp.spring.app.model.budget_detail.repos.BudgetDetailRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.util.NotFoundException;
import com.grepp.spring.util.ReferencedWarning;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final MemberRepository memberRepository;
    private final BudgetDetailRepository budgetDetailRepository;

    public BudgetService(final BudgetRepository budgetRepository,
            final MemberRepository memberRepository,
            final BudgetDetailRepository budgetDetailRepository) {
        this.budgetRepository = budgetRepository;
        this.memberRepository = memberRepository;
        this.budgetDetailRepository = budgetDetailRepository;
    }

    public List<BudgetDTO> findAll() {
        final List<Budget> budgets = budgetRepository.findAll(Sort.by("budgetId"));
        return budgets.stream()
                .map(budget -> mapToDTO(budget, new BudgetDTO()))
                .toList();
    }

    public BudgetDTO get(final Long budgetId) {
        return budgetRepository.findById(budgetId)
                .map(budget -> mapToDTO(budget, new BudgetDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final BudgetDTO budgetDTO) {
        final Budget budget = new Budget();
        mapToEntity(budgetDTO, budget);
        return budgetRepository.save(budget).getBudgetId();
    }

    public void update(final Long budgetId, final BudgetDTO budgetDTO) {
        final Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(budgetDTO, budget);
        budgetRepository.save(budget);
    }

    public void delete(final Long budgetId) {
        budgetRepository.deleteById(budgetId);
    }

    // 현재 달의 총 수입과 총 지출 조회
    public BigDecimal[] getCurrentMonthTotal(Long memberId) {
        YearMonth currentYearMonth = YearMonth.now();
        LocalDate startOfMonth = currentYearMonth.atDay(1);
        LocalDate endOfMonth = currentYearMonth.atEndOfMonth();
        
        // Member 객체를 먼저 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found"));
        
        List<Budget> monthlyBudgets = budgetRepository.findAllByMemberAndDateBetweenOrderByDateAsc(member, startOfMonth, endOfMonth);
        
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        
        for (Budget budget : monthlyBudgets) {
            if (budget.getTotalIncome() != null) {
                totalIncome = totalIncome.add(budget.getTotalIncome());
            }
            if (budget.getTotalExpense() != null) {
                totalExpense = totalExpense.add(budget.getTotalExpense());
            }
        }
        
        return new BigDecimal[]{totalIncome, totalExpense};
    }

    private BudgetDTO mapToDTO(final Budget budget, final BudgetDTO budgetDTO) {
        budgetDTO.setBudgetId(budget.getBudgetId());
        budgetDTO.setDate(budget.getDate());
        budgetDTO.setTotalIncome(budget.getTotalIncome());
        budgetDTO.setTotalExpense(budget.getTotalExpense());
        budgetDTO.setTargetExpense(budget.getTargetExpense());
        budgetDTO.setMember(budget.getMember() == null ? null : budget.getMember().getMemberId());
        return budgetDTO;
    }

    private Budget mapToEntity(final BudgetDTO budgetDTO, final Budget budget) {
        budget.setDate(budgetDTO.getDate());
        budget.setTotalIncome(budgetDTO.getTotalIncome());
        budget.setTotalExpense(budgetDTO.getTotalExpense());
        budget.setTargetExpense(budgetDTO.getTargetExpense());
        final Member member = budgetDTO.getMember() == null ? null : memberRepository.findById(budgetDTO.getMember())
                .orElseThrow(() -> new NotFoundException("member not found"));
        budget.setMember(member);
        return budget;
    }

    public ReferencedWarning getReferencedWarning(final Long budgetId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(NotFoundException::new);
        final BudgetDetail budgetBudgetDetail = budgetDetailRepository.findFirstByBudget(budget);
        if (budgetBudgetDetail != null) {
            referencedWarning.setKey("budget.budgetDetail.budget.referenced");
            referencedWarning.addParam(budgetBudgetDetail.getBudgetDetailId());
            return referencedWarning;
        }
        return null;
    }

}
