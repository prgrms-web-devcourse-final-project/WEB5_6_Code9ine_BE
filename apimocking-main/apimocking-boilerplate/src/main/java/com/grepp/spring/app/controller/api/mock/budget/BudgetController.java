package com.grepp.spring.app.controller.api.mock.budget;

import com.grepp.spring.app.model.budget.model.BudgetAnalyzeResponseDto;
import com.grepp.spring.app.model.budget.model.BudgetCalenderResponseDto;
import com.grepp.spring.app.model.budget.model.BudgetCategorySummary;
import com.grepp.spring.app.model.budget.model.BudgetDaySummary;
import com.grepp.spring.app.model.budget.model.BudgetGoal;
import com.grepp.spring.app.model.budget.model.BudgetMonthlyExpense;
import com.grepp.spring.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    @Operation(summary = "달력 페이지 조회")
    @GetMapping("/calendar")
    public ApiResponse<BudgetCalenderResponseDto> getMonthlySummary(@RequestParam("yearmonth") String yearmonth) {
        List<BudgetDaySummary> days = List.of(
            new BudgetDaySummary(yearmonth+"-01", 50000, 32000, 18000),
            new BudgetDaySummary(yearmonth+"-02", 0, 28000, -28000),
            new BudgetDaySummary(yearmonth+"-03", 10000, 10000, 0)
        );

        BudgetCalenderResponseDto response = new BudgetCalenderResponseDto(
            yearmonth,
            1250000,
            870000,
            days
        );

        return ApiResponse.success(response);
    }

    @Operation(summary = "가계부 분석 페이지")
    @GetMapping("/analyze")
    public ApiResponse<BudgetAnalyzeResponseDto>  getDashboard() {

        BudgetGoal goal = new BudgetGoal(
            "아이패드 에어", "https://example.com/ipad.png", 890000
        );

        List<BudgetMonthlyExpense> monthlyExpenses = List.of(
            new BudgetMonthlyExpense("2025-02", 1410000),
            new BudgetMonthlyExpense("2025-03", 1340000),
            new BudgetMonthlyExpense("2025-04", 1680000),
            new BudgetMonthlyExpense("2025-05", 1220000),
            new BudgetMonthlyExpense("2025-06", 1470000),
            new BudgetMonthlyExpense("2025-07", 1250000)
        );

        List<BudgetCategorySummary> categorySummary = List.of(
            new BudgetCategorySummary("식비", "🍔", 530000),
            new BudgetCategorySummary("교통", "🚌", 120000),
            new BudgetCategorySummary("문화생활", "🎬", 180000)
        );

        BudgetAnalyzeResponseDto response = new BudgetAnalyzeResponseDto(
            "2025-07",
            2000000,
            1250000,
            goal,
            1250000,
            monthlyExpenses,
            categorySummary,
            220000,
            1500000
        );

        return ApiResponse.success(response);
    }
}
