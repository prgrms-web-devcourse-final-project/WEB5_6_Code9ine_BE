package com.grepp.spring.app.controller.api.budget;

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

    @GetMapping("/details")
    public ApiResponse<Map<String, Object>> getSummary() {

        // expenses 리스트
        List<Map<String, Object>> expenses = new ArrayList<>();

        Map<String, Object> expense1 = new HashMap<>();
        expense1.put("id", 1);
        expense1.put("category", "식비");
        expense1.put("categoryIcon", "🍔");
        expense1.put("content", "햄버거");
        expense1.put("date", "2025-07-03");
        expense1.put("price", 8700);
        expense1.put("repeatCycle", "NONE");

        Map<String, Object> expense2 = new HashMap<>();
        expense2.put("id", 2);
        expense2.put("category", "교통");
        expense2.put("categoryIcon", "🚌");
        expense2.put("content", "버스");
        expense2.put("date", "2025-07-02");
        expense2.put("price", 1250);
        expense2.put("repeatCycle", "MONTHLY");

        expenses.add(expense1);
        expenses.add(expense2);

        // data
        Map<String, Object> data = new HashMap<>();
        data.put("yearMonth", "2025-07");
        data.put("totalIncome", 1200000);
        data.put("totalExpense", 870000);
        data.put("totalDifference", 330000);
        data.put("expenses", expenses);

        return ApiResponse.success(data);
    }

    @GetMapping("/calendar")
    public ApiResponse<Map<String, Object>> getMonthlySummary() {
        // 날짜별 항목 리스트 생성
        List<Map<String, Object>> days = new ArrayList<>();

        Map<String, Object> day1 = new HashMap<>();
        day1.put("date", "2025-07-01");
        day1.put("income", 50000);
        day1.put("expense", 32000);
        day1.put("difference", 18000);
        days.add(day1);

        Map<String, Object> day2 = new HashMap<>();
        day2.put("date", "2025-07-02");
        day2.put("income", 0);
        day2.put("expense", 28000);
        day2.put("difference", -28000);
        days.add(day2);

        Map<String, Object> day3 = new HashMap<>();
        day3.put("date", "2025-07-03");
        day3.put("income", 10000);
        day3.put("expense", 10000);
        day3.put("difference", 0);
        days.add(day3);

        // data 객체 생성
        Map<String, Object> data = new HashMap<>();
        data.put("totalIncome", 1250000);
        data.put("totalExpense", 870000);
        data.put("month", "2025-07");
        data.put("days", days);

        return ApiResponse.success(data);
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
