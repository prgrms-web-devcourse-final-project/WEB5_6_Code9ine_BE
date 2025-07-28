package com.grepp.spring.app.model.geminichatbot.service;

import com.grepp.spring.app.model.budget_detail.domain.BudgetDetail;
import com.grepp.spring.app.model.geminichatbot.repos.GeminiChatbotBudgetDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeminiChatbotService {
    private final GeminiChatbotBudgetDetailRepository budgetDetailRepository;

    public String getSpendingDataForLastMonth(Long memberId) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusMonths(1).plusDays(1);
        List<BudgetDetail> details = budgetDetailRepository.findExpenseDetailsByMemberIdAndDateBetween(memberId, start, end);
        
        // 카테고리별 총액 계산
        Map<String, Double> categoryTotals = details.stream()
            .collect(Collectors.groupingBy(
                BudgetDetail::getCategory,
                Collectors.summingDouble(detail -> detail.getPrice().doubleValue())
            ));
        
        StringBuilder sb = new StringBuilder();
        sb.append("사용자 최근 한 달 지출 내역:\n");
        for (BudgetDetail d : details) {
            sb.append(d.getDate())
              .append(": ")
              .append(d.getCategory())
              .append(" ")
              .append(d.getPrice().intValue())
              .append("원\n");
        }
        
        sb.append("\n카테고리별 총 지출:\n");
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            sb.append(entry.getKey())
              .append(": ")
              .append(String.format("%.0f", entry.getValue()))
              .append("원\n");
        }
        
        return sb.toString();
    }

    // 전체 사용자의 카테고리별 평균 지출 계산
    public String getAverageSpendingByCategory() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusMonths(1).plusDays(1);
        List<BudgetDetail> allDetails = budgetDetailRepository.findAllExpenseDetailsByDateBetween(start, end);
        
        // 카테고리별로 그룹화하여 평균 계산
        Map<String, Double> categoryAverages = allDetails.stream()
            .collect(Collectors.groupingBy(
                BudgetDetail::getCategory,
                Collectors.averagingDouble(detail -> detail.getPrice().doubleValue())
            ));
        
        StringBuilder sb = new StringBuilder();
        sb.append("전체 사용자 카테고리별 평균 지출 (최근 1개월):\n");
        for (Map.Entry<String, Double> entry : categoryAverages.entrySet()) {
            sb.append(entry.getKey())
              .append(": ")
              .append(String.format("%.0f", entry.getValue()))
              .append("원\n");
        }
        return sb.toString();
    }
} 