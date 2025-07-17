package com.grepp.spring.app.model.budget.service;

import com.grepp.spring.app.model.budget.domain.Budget;
import com.grepp.spring.app.model.budget.model.BudgetCalendarResponseDto;
import com.grepp.spring.app.model.budget.model.BudgetDaySummary;
import com.grepp.spring.app.model.budget_detail.repos.BudgetDetailRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetCalendarService {

    private final BudgetDetailRepository budgetDetailRepository;

    public BudgetCalendarResponseDto getMonthlySummary(Long memberId, String yearmonth) {

        LocalDate today = LocalDate.now();

        Map<String, BigDecimal> totals = getMonthlySummaryUpToToday(memberId);
        List<BudgetDaySummary> days = getDailySummaries(memberId,yearmonth);

        return BudgetCalendarResponseDto.builder()
            .month(yearmonth)
            .totalIncome(totals.get("totalIncome"))
            .totalExpense(totals.get("totalExpense"))
            .days(days)
            .build();
    }

    public Map<String, BigDecimal> getMonthlySummaryUpToToday(Long memberId) {

        LocalDate today = LocalDate.now();
        LocalDate start = today.withDayOfMonth(1);

        // 1. 전체 총합
        List<Budget> budgets = budgetDetailRepository.findTotalIncomeAndExpense(memberId,start,
            today);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Budget budget : budgets) {
            if (budget.getTotalIncome() != null) {
                totalIncome = totalIncome.add(budget.getTotalIncome());
            }
            if (budget.getTotalExpense() != null) {
                totalExpense = totalExpense.add(budget.getTotalExpense());
            }
        }

        return Map.of(
            "totalIncome", totalIncome,
            "totalExpense", totalExpense
        );
    }

    public List<BudgetDaySummary> getDailySummaries(Long memberId,String yearmonth)
    {
        YearMonth ym = YearMonth.parse(yearmonth); // "2025-07"
        LocalDate start = ym.atDay(1);
        LocalDate end= start.withDayOfMonth(start.lengthOfMonth());
        List<Object[]> rows = budgetDetailRepository.findDailySummary(memberId, start, end);

        // 날짜별로 합계 저장
        Map<LocalDate, BudgetDaySummary> map = new HashMap<>();

        for (Object[] row : rows) {
            LocalDate date = (LocalDate) row[0];
            String type = (String) row[1];
            BigDecimal amount = new BigDecimal(row[2].toString());

            if(!map.containsKey(date))
            {
                map.put(date, BudgetDaySummary.builder()
                    .date(date)
                    .income(BigDecimal.ZERO)
                    .expense(BigDecimal.ZERO)
                    .difference(BigDecimal.ZERO)
                    .build()
                );
            }

            BudgetDaySummary summary = map.get(date);

            if ("수입".equals(type)) {
                summary.setIncome(amount);
            } else if ("지출".equals(type)) {
                summary.setExpense(amount);
            }

            // 매번 수입-지출 계산
            summary.setDifference(summary.getIncome().subtract(summary.getExpense()));

        }
        return map.values().stream()
            .sorted(Comparator.comparing(BudgetDaySummary::getDate))
            .collect(Collectors.toList());
    }
}
