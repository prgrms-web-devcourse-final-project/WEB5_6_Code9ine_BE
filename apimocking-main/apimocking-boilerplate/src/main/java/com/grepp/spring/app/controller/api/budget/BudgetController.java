package com.grepp.spring.app.controller.api.budget;

import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.budget.model.BudgetAnalyzeResponseDto;
import com.grepp.spring.app.model.budget.model.BudgetCalendarResponseDto;
import com.grepp.spring.app.model.budget.model.BudgetCategorySummary;
import com.grepp.spring.app.model.budget.model.BudgetDaySummary;
import com.grepp.spring.app.model.budget.model.BudgetGoal;
import com.grepp.spring.app.model.budget.model.BudgetMonthlyExpense;
import com.grepp.spring.app.model.budget.service.BudgetAnalyzeService;
import com.grepp.spring.app.model.budget.service.BudgetCalendarService;
import com.grepp.spring.app.model.budget.service.BudgetService;
import com.grepp.spring.app.model.budget_detail.service.BudgetDetailService;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budget")
public class BudgetController {

    private final BudgetCalendarService budgetCalendarService;
    private final BudgetAnalyzeService budgetAnalyzeService;


    @Operation(summary = "달력 페이지 조회")
    @GetMapping("/calendar")
    public ApiResponse<BudgetCalendarResponseDto> getMonthlySummary(@AuthenticationPrincipal Principal principal, @RequestParam("yearmonth") String yearmonth) {

        BudgetCalendarResponseDto monthlySummary = budgetCalendarService.getMonthlySummary(
            principal.getMemberId(), yearmonth);
        return ApiResponse.success(monthlySummary);
    }

    @Operation(summary = "가계부 분석 페이지")
    @GetMapping("/analyze")
    public ApiResponse<BudgetAnalyzeResponseDto>  getDashboard(@AuthenticationPrincipal Principal principal) {
        LocalDate today = LocalDate.now();
        YearMonth yearMonth = YearMonth.from(today);

        BudgetGoal goal = budgetAnalyzeService.getMemberGoal(principal.getMemberId());
        Map<String, BigDecimal> totals = budgetCalendarService.getMonthlySummaryUpToToday(
            principal.getMemberId());

        List<BudgetMonthlyExpense> monthlyExpenses = budgetAnalyzeService.getMonthlyExpense(
            principal.getUsername());

        List<BudgetCategorySummary> categorySummary = budgetAnalyzeService.getThisMonthCategoryExpense(
            principal.getMemberId());

        int size = monthlyExpenses.size();
        BigDecimal recentmonth = monthlyExpenses.get(size - 1).getAmount();
        BigDecimal beforemonth= monthlyExpenses.get(size - 2).getAmount();



        BudgetAnalyzeResponseDto response = new BudgetAnalyzeResponseDto(
            String.valueOf(yearMonth),
            totals.get("totalIncome"),
            totals.get("totalExpense"),
            goal,
            BigDecimal.valueOf(1250000),
            monthlyExpenses,
            categorySummary,
            beforemonth.subtract(recentmonth),
            BigDecimal.valueOf(1500000)
        );

        return ApiResponse.success(response);
    }
}
