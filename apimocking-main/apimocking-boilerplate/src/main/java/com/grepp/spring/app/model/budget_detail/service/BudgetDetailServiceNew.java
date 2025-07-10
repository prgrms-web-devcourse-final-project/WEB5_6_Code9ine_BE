package com.grepp.spring.app.model.budget_detail.service;

import com.grepp.spring.app.model.budget.domain.Budget;
import com.grepp.spring.app.model.budget.repos.BudgetRepository;
import com.grepp.spring.app.model.budget_detail.domain.BudgetDetail;
import com.grepp.spring.app.model.budget_detail.model.BudgetDetailExpenseResponseDTO;
import com.grepp.spring.app.model.budget_detail.model.BudgetDetailRequestDTO;
import com.grepp.spring.app.model.budget_detail.model.Item;
import com.grepp.spring.app.model.budget_detail.repos.BudgetDetailRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetDetailServiceNew {

    private final BudgetRepository budgetRepository;
    private final BudgetDetailRepository budgetDetailRepository;
    private final MemberRepository memberRepository;


    public BudgetDetailExpenseResponseDTO findExpensesByDate(String username, LocalDate date) {

        Member member = memberRepository.findById(Long.valueOf(username))
            .orElseThrow(() -> new RuntimeException("해당 ID의 회원이 존재하지 않습니다."));

        // 해당 유저의 해당 날짜 Budget 찾기
        Budget budget = budgetRepository.findByDateWithDetails(date, member)
            .orElseThrow(() -> new RuntimeException("해당 날짜의 예산이 없습니다."));

        // Budget에 연결된 BudgetDetail 꺼내기
        List<Item> items = budget.getBudgetDetails().stream()
            .map(detail -> new Item(
                detail.getBudgetDetailId(),
                detail.getCategory(),
                getIconForCategory(detail.getCategory()),  // 필요 시 구현
                detail.getContent(),
                detail.getAmount()
            ))
            .toList();

        return new BudgetDetailExpenseResponseDTO(items);
    }

    // 카테고리에 따른 아이콘 매핑 예시
    private String getIconForCategory(String category) {
        return switch (category) {
            case "식비" -> "🍱";
            case "카페" -> "☕";
            case "교통" -> "🚇";
            default -> "💸";
        };
    }





    @Transactional
    public void registerExpense(String username, BudgetDetailRequestDTO dto) {

        Member member = memberRepository.findById(Long.valueOf(username))
            .orElseThrow(() -> new RuntimeException("해당 ID의 회원이 존재하지 않습니다."));

        // 1. 해당 날짜의 가계부(Budget) 조회
        Budget budget = budgetRepository.findByDateAndMember(dto.getDate(), member)
            .orElseGet(() -> {
                Budget newBudget = new Budget();
                newBudget.setDate(dto.getDate());
                newBudget.setMember(member);
                return budgetRepository.save(newBudget);
            });

        // 2. 지출 세부사항(BudgetDetail) 생성
        BudgetDetail detail = BudgetDetail.builder()
            .budget(budget)
            .category(dto.getCategory())
            .date(dto.getDate())
            .amount(dto.getAmount())
            .content(dto.getContent())
            .type(dto.getType())
            .repeatCycle(dto.getRepeatCycle())
            .build();

        // 3. 저장
        budgetDetailRepository.save(detail);
    }


}
