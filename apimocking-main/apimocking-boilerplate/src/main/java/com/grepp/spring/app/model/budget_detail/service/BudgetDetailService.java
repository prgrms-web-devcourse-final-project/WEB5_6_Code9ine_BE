package com.grepp.spring.app.model.budget_detail.service;

import com.grepp.spring.app.model.budget.domain.Budget;
import com.grepp.spring.app.model.budget.repos.BudgetRepository;
import com.grepp.spring.app.model.budget_detail.domain.BudgetDetail;
import com.grepp.spring.app.model.budget_detail.model.BudgetDetailDto;
import com.grepp.spring.app.model.budget_detail.model.BudgetDetailRequestDTO;
import com.grepp.spring.app.model.budget_detail.model.BudgetDetailResponseDto;
import com.grepp.spring.app.model.budget_detail.model.UpdatedBudgetDetailResponseDto;
import com.grepp.spring.app.model.budget_detail.repos.BudgetDetailRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BudgetDetailService {

    private final BudgetRepository budgetRepository;
    private final BudgetDetailRepository budgetDetailRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public BudgetDetailResponseDto findBudgetDetailByDate(String username, String date) {

        Member member = memberRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("해당 ID의 회원이 존재하지 않습니다."));

        // 해당 유저의 해당 날짜 Budget 찾기
        Budget budget = budgetRepository.findByDateWithDetails(LocalDate.parse(date), member)
            .orElseThrow(() -> new RuntimeException("해당 날짜의 예산이 없습니다."));

        // Budget에 연결된 BudgetDetail 꺼내기
        List<BudgetDetailDto> details = budget.getBudgetDetails().stream()
            .map(BudgetDetailDto::from)
            .toList();

        return new BudgetDetailResponseDto(details);
    }

    @Transactional
    public void registerBudgetDetail(String username,BudgetDetailRequestDTO dto) {

        Member member = memberRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("해당 ID의 회원이 존재하지 않습니다."));

        // 1. 해당 날짜의 가계부(Budget) 조회
        Budget budget = budgetRepository.findByDateAndMember(LocalDate.parse(dto.getDate()), member)
            .orElseGet(() -> {
                Budget newBudget = new Budget();
                newBudget.setDate(LocalDate.parse(dto.getDate()));
                newBudget.setMember(member);
                return budgetRepository.save(newBudget);
            });

        // 2. 지출 세부사항(BudgetDetail) 생성
        BudgetDetail detail = BudgetDetail.builder()
            .budget(budget)
            .category(dto.getCategory())
            .date(LocalDate.parse(dto.getDate()))
            .price(dto.getPrice())
            .content(dto.getContent())
            .type(dto.getType())
            .repeatCycle(dto.getRepeatCycle())
            .build();

        budgetDetailRepository.save(detail);
    }

    // 지출수정
    @Transactional
    public UpdatedBudgetDetailResponseDto updateBudgetDetail(Long detailId, BudgetDetailRequestDTO dto) {

        // 1. 수정할 BudgetDetail 엔티티 조회
        BudgetDetail budgetDetail = budgetDetailRepository.findById(detailId)
            .orElseThrow(() -> new RuntimeException("해당 지출 내역이 존재하지 않습니다."));

        Member member = budgetDetail.getBudget().getMember(); // 기존 멤버 가져오기
        LocalDate newDate = LocalDate.parse(dto.getDate());

        if (!budgetDetail.getBudget().getDate().equals(newDate)) {
            // 2. 기존 Budget에서 지출 제거
            Budget oldBudget = budgetDetail.getBudget();
            oldBudget.getBudgetDetails().remove(budgetDetail);

            // 3. 새로운 날짜에 해당하는 Budget을 찾거나 생성
            Budget newBudget = budgetRepository.findByDateAndMember(newDate, member)
                .orElseGet(() -> {
                    Budget created = new Budget();
                    created.setDate(newDate);
                    created.setMember(member);
                    return budgetRepository.save(created);
                });

            // 4. BudgetDetail의 Budget을 새로운 것으로 바꾸기
            budgetDetail.setBudget(newBudget);
            //newBudget.getBudgetDetails().add(budgetDetail);

            // 5. 예전 Budget이 고아가 되었으면 삭제
            if (oldBudget.getBudgetDetails().isEmpty()) {
                budgetRepository.delete(oldBudget);
            }
        }
        budgetDetail.updateFromDto(dto);

        UpdatedBudgetDetailResponseDto updatedExpenseResponseDto = new UpdatedBudgetDetailResponseDto(
            detailId,
            budgetDetail.getType(),
            budgetDetail.getDate(),
            budgetDetail.getCategory(),
            budgetDetail.getPrice(),
            budgetDetail.getContent(),
            budgetDetail.getRepeatCycle()
        );

        return updatedExpenseResponseDto;
    }




}
