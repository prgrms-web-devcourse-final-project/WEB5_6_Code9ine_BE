package com.grepp.spring.app.controller.api.budget;

import com.grepp.spring.app.model.budget.model.BudgetCalenderResponseDto;
import com.grepp.spring.app.model.budget.model.BudgetDaySummary;
import com.grepp.spring.infra.response.ApiResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {


    @GetMapping("/calendar")
    public ApiResponse<BudgetCalenderResponseDto> getMonthlySummary() {
        List<BudgetDaySummary> days = List.of(
            new BudgetDaySummary("2025-07-01", 50000, 32000, 18000),
            new BudgetDaySummary("2025-07-02", 0, 28000, -28000),
            new BudgetDaySummary("2025-07-03", 10000, 10000, 0)
        );

        BudgetCalenderResponseDto response = new BudgetCalenderResponseDto(
            "2025-07",
            1250000,
            870000,
            days
        );

        return ApiResponse.success(response);
    }

    @GetMapping("/analyze")
    public ApiResponse<Map<String, Object>>  getDashboard() {

        // goal 정보
        Map<String, Object> goal = new HashMap<>();
        goal.put("itemName", "아이패드 에어");
        goal.put("itemImage", "https://example.com/ipad.png");
        goal.put("itemPrice", 890000);

        // monthlyExpenses 리스트
        List<Map<String, Object>> monthlyExpenses = List.of(
            Map.of("month", "2025-02", "amount", 1410000),
            Map.of("month", "2025-03", "amount", 1340000),
            Map.of("month", "2025-04", "amount", 1680000),
            Map.of("month", "2025-05", "amount", 1220000),
            Map.of("month", "2025-06", "amount", 1470000),
            Map.of("month", "2025-07", "amount", 1250000)
        );

        // categorySummary 리스트
        List<Map<String, Object>> categorySummary = List.of(
            Map.of("category", "식비", "categoryIcon", "🍔", "totalAmount", 530000),
            Map.of("category", "교통", "categoryIcon", "🚌", "totalAmount", 120000),
            Map.of("category", "문화생활", "categoryIcon", "🎬", "totalAmount", 180000)
        );

        // data 객체 생성
        Map<String, Object> data = new HashMap<>();
        data.put("yearMonth", "2025-07");
        data.put("totalIncome", 2000000);
        data.put("totalExpense", 1250000);
        data.put("goal", goal);
        data.put("currentMonthExpense", 1250000);
        data.put("monthlyExpenses", monthlyExpenses);
        data.put("categorySummary", categorySummary);
        data.put("savedComparedToLastMonth", 220000);
        data.put("totalsavedAmount", 1500000);

        return ApiResponse.success(data);
    }
}
