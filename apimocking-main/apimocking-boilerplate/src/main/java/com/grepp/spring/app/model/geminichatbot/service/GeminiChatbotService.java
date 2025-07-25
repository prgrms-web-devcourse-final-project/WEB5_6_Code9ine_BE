package com.grepp.spring.app.model.geminichatbot.service;

import com.grepp.spring.app.model.budget_detail.domain.BudgetDetail;
import com.grepp.spring.app.model.geminichatbot.repos.GeminiChatbotBudgetDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiChatbotService {
    private final GeminiChatbotBudgetDetailRepository budgetDetailRepository;

    public String getSpendingDataForLastMonth(Long memberId) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusMonths(1).plusDays(1);
        List<BudgetDetail> details = budgetDetailRepository.findExpenseDetailsByMemberIdAndDateBetween(memberId, start, end);
        StringBuilder sb = new StringBuilder();
        for (BudgetDetail d : details) {
            sb.append(d.getDate())
              .append(": ")
              .append(d.getCategory())
              .append(" ")
              .append(d.getPrice().intValue())
              .append("원\n");
        }
        return sb.toString();
    }
} 