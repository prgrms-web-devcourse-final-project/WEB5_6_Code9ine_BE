package com.grepp.spring.app.controller.api.budget;

import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.budget.model.BudgetAnalyzeResponseDto;
import com.grepp.spring.app.model.budget.model.BudgetCalendarResponseDto;
import com.grepp.spring.app.model.budget.model.BudgetCategorySummary;
import com.grepp.spring.app.model.budget.model.BudgetDaySummary;
import com.grepp.spring.app.model.budget.model.BudgetGoal;
import com.grepp.spring.app.model.budget.model.BudgetMonthlyExpense;
import com.grepp.spring.app.model.budget.service.BudgetCalendarService;
import com.grepp.spring.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
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
@Profile("!mock")
public class BudgetController {

    private final BudgetCalendarService budgetCalendarService;

    @Operation(summary = "달력 페이지 조회")
    @GetMapping("/calendar")
    public ApiResponse<BudgetCalendarResponseDto> getMonthlySummary(@AuthenticationPrincipal Principal principal, @RequestParam("yearmonth") String yearmonth) {

        BudgetCalendarResponseDto monthlySummary = budgetCalendarService.getMonthlySummary(
            principal.getMemberId(), yearmonth);
        return ApiResponse.success(monthlySummary);
    }

    @Operation(summary = "가계부 분석 페이지")
    @GetMapping("/analyze")
    public ApiResponse<BudgetAnalyzeResponseDto>  getDashboard() {

        BudgetGoal goal = new BudgetGoal(
            "아이패드 에어", "https://example.com/ipad.png", BigDecimal.valueOf(890000)
        );

        List<BudgetMonthlyExpense> monthlyExpenses = List.of(
            new BudgetMonthlyExpense("2025-02", BigDecimal.valueOf(1410000)),
            new BudgetMonthlyExpense("2025-03", BigDecimal.valueOf(1340000)),
            new BudgetMonthlyExpense("2025-04", BigDecimal.valueOf(1680000)),
            new BudgetMonthlyExpense("2025-05", BigDecimal.valueOf(1220000)),
            new BudgetMonthlyExpense("2025-06", BigDecimal.valueOf(1470000)),
            new BudgetMonthlyExpense("2025-07", BigDecimal.valueOf(1250000))
        );

        List<BudgetCategorySummary> categorySummary = List.of(
            new BudgetCategorySummary("식비", BigDecimal.valueOf(530000)),
            new BudgetCategorySummary("교통", BigDecimal.valueOf(120000)),
            new BudgetCategorySummary("문화생활", BigDecimal.valueOf(180000))
        );

        BudgetAnalyzeResponseDto response = new BudgetAnalyzeResponseDto(
            "2025-07",
            BigDecimal.valueOf(2000000),
            BigDecimal.valueOf(1250000),
            goal,
            BigDecimal.valueOf(1250000),
            monthlyExpenses,
            categorySummary,
            BigDecimal.valueOf(220000),
            BigDecimal.valueOf(1500000)
        );

        return ApiResponse.success(response);
    }
}
