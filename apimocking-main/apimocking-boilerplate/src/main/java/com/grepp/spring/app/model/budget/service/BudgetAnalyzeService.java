package com.grepp.spring.app.model.budget.service;

import com.grepp.spring.app.model.budget.model.BudgetCategorySummary;
import com.grepp.spring.app.model.budget.model.BudgetGoal;
import com.grepp.spring.app.model.budget.model.BudgetMonthlyExpense;
import com.grepp.spring.app.model.budget.repos.BudgetRepository;
import com.grepp.spring.app.model.budget_detail.repos.BudgetDetailRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetAnalyzeService {

    private final MemberRepository memberRepository;
    private final BudgetRepository budgetRepository;
    private final BudgetDetailRepository budgetDetailRepository;

    public List<BudgetMonthlyExpense> getMonthlyExpense(String username) {
        Member member = memberRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        LocalDate now = LocalDate.now();
        LocalDate start = now.minusMonths(5).withDayOfMonth(1);  // 6개월 전 첫날
        LocalDate end = now; // 오늘

        // 1. 최근 6개월 문자열 리스트 생성
        List<String> last6Months = IntStream.rangeClosed(0, 5)
            .mapToObj(i -> start.plusMonths(i).format(DateTimeFormatter.ofPattern("yyyy-MM")))
            .collect(Collectors.toList());

        // 2. DB에서 월별 합계 조회
        List<Object[]> results = budgetRepository.findMonthlyExpenseSumByEmailAndDateRange(member.getEmail(), start, end);

        // 3. DB 결과 Map으로 변환
        Map<String, BigDecimal> dbMap = results.stream()
            .collect(Collectors.toMap(
                r -> (String) r[0],
                r -> r[1] != null ? new BigDecimal(r[1].toString()) : BigDecimal.ZERO
            ));

        // 4. 전체 6개월 리스트 기준으로 결과 생성, 없으면 0 넣기
        return last6Months.stream()
            .map(month -> new BudgetMonthlyExpense(month, dbMap.getOrDefault(month, BigDecimal.ZERO)))
            .collect(Collectors.toList());
    }

    public List<BudgetCategorySummary> getThisMonthCategoryExpense(Long memberId) {

        List<String> categories = List.of("식비","교통","여가","경조사","쇼핑","교육","건강","기타","생활","주거/통신");
        LocalDate today = LocalDate.now();
        LocalDate start = today.withDayOfMonth(1);
        LocalDate end = today;

        List<BudgetCategorySummary> dbResults = budgetDetailRepository.findMonthlyCategoryExpense(memberId, start, end);
        Map<String, BigDecimal> dbMap = dbResults.stream()
            .collect(Collectors.toMap(
                BudgetCategorySummary::getCategory,
                BudgetCategorySummary::getTotalAmount
            ));

        List<BudgetCategorySummary> fullResults = categories.stream()
            .map(cat -> new BudgetCategorySummary(cat, dbMap.getOrDefault(cat, BigDecimal.ZERO)))
            .collect(Collectors.toList());

        return fullResults;
    }

    public BudgetGoal getMemberGoal(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));

        return new BudgetGoal(
            member.getGoalStuff(),
            member.getGoalAmount()
        );
    }

    public BigDecimal getTotalExpenseUntilToday(Long memberId) {
        LocalDate today = LocalDate.now();

        BigDecimal income = budgetRepository.sumTotalIncomeByMemberUntilToday(memberId, today)
            .orElse(BigDecimal.ZERO);

        BigDecimal expense = budgetRepository.sumTotalExpenseByMemberUntilToday(memberId, today)
            .orElse(BigDecimal.ZERO);

        return income.subtract(expense);
    }

}
