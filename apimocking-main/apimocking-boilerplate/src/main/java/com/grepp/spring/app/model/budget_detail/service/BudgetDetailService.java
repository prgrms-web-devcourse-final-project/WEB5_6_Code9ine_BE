package com.grepp.spring.app.model.budget_detail.service;

import com.grepp.spring.app.model.budget.domain.Budget;
import com.grepp.spring.app.model.budget.repos.BudgetRepository;
import com.grepp.spring.app.model.budget_detail.domain.BudgetDetail;
import com.grepp.spring.app.model.budget_detail.model.BudgetDetailDto;
import com.grepp.spring.app.model.budget_detail.model.BudgetDetailRequestDTO;
import com.grepp.spring.app.model.budget_detail.model.BudgetDetailResponseDto;
import com.grepp.spring.app.model.budget_detail.model.UpdatedBudgetDetailResponseDto;
import com.grepp.spring.app.model.budget_detail.repos.BudgetDetailRepository;
import com.grepp.spring.app.model.challenge.domain.Challenge;
import com.grepp.spring.app.model.challenge.repos.ChallengeRepository;
import com.grepp.spring.app.model.challenge_count.domain.ChallengeCount;
import com.grepp.spring.app.model.challenge_count.repos.ChallengeCountRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BudgetDetailService {

    private final BudgetRepository budgetRepository;
    private final BudgetDetailRepository budgetDetailRepository;
    private final MemberRepository memberRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeCountRepository challengeCountRepository;

    @Transactional(readOnly = true)
    public BudgetDetailResponseDto findBudgetDetailByDate(String username, String date) {

        Member member = memberRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("해당 ID의 회원이 존재하지 않습니다."));

        // 해당 유저의 해당 날짜 Budget 찾기
        Budget budget = budgetRepository.findByDateWithDetails(LocalDate.parse(date), member)
            .orElseThrow(() -> new RuntimeException("해당 날짜의 예산이 없습니다."));

        // Budget에 연결된 BudgetDetail 꺼내기
        List<BudgetDetailDto> details = budget.getBudgetDetails().stream()
            .sorted(Comparator.comparing(BudgetDetail::getCreatedAt).reversed()) // 내림차순
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
            .orElseGet(() -> budgetRepository.save(Budget.create(LocalDate.parse(dto.getDate()), member)));

        budget.addAmount(dto.getType(),dto.getPrice());

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

        // 만원의행복검증
        if(LocalDate.parse(dto.getDate()).equals(LocalDate.now()))
        {
            handle_under10000Challenge(member, budget, false,true);


        }

    }

    // 지출수정
    @Transactional
    public UpdatedBudgetDetailResponseDto updateBudgetDetail(Long detailId, BudgetDetailRequestDTO dto) {

        // 1. 수정할 BudgetDetail 엔티티 조회
        BudgetDetail budgetDetail = budgetDetailRepository.findById(detailId)
            .orElseThrow(() -> new RuntimeException("해당 지출 내역이 존재하지 않습니다."));

        Budget oldBudget = budgetDetail.getBudget();
        Member member = oldBudget.getMember();
        LocalDate newDate = LocalDate.parse(dto.getDate());

        String oldType = budgetDetail.getType();
        BigDecimal oldPrice = budgetDetail.getPrice();

        // 기존 Budget의 총합에서 기존 금액 차감
        oldBudget.minusBudgetTotal(oldType, oldPrice);

        Budget newBudget = oldBudget;
        boolean empty = false;
        // 2. 날짜가 바뀌었으면 새 Budget으로 이동
        if (!oldBudget.getDate().equals(newDate)) {
            oldBudget.getBudgetDetails().remove(budgetDetail);

            newBudget = budgetRepository.findByDateAndMember(newDate, member)
                .orElseGet(() -> budgetRepository.save(Budget.create(LocalDate.parse(dto.getDate()), member)));

            budgetDetail.setBudget(newBudget);
            empty = oldBudget.getBudgetDetails().isEmpty();
            if (empty) {
                budgetRepository.delete(oldBudget);
            }

        }

        // BudgetDetail 업데이트
        budgetDetail.updateFromDto(dto);

        // 새 Budget에 새 금액 더함
        newBudget.addAmount(dto.getType(), dto.getPrice());

        // 만원의 행복 챌린지 검증
        if(LocalDate.parse(dto.getDate()).equals(LocalDate.now())){
            handle_under10000Challenge(member, newBudget, false ,true);
        }
        else{
            handle_under10000Challenge(member, oldBudget, empty ,true);
        }



        return new UpdatedBudgetDetailResponseDto(
            detailId,
            budgetDetail.getType(),
            budgetDetail.getDate(),
            budgetDetail.getCategory(),
            budgetDetail.getPrice(),
            budgetDetail.getContent(),
            budgetDetail.getRepeatCycle()
        );

    }

    @Transactional
    public void deleteBudgetDetail(Long memberId, Long detailId) {

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        BudgetDetail detail = budgetDetailRepository.findById(detailId)
            .orElseThrow(() -> new RuntimeException("해당 지출 내역이 존재하지 않습니다."));

        Budget budget = detail.getBudget();
        budget.minusBudgetTotal(detail.getType(), detail.getPrice());

        budgetDetailRepository.delete(detail);

        boolean hasOtherDetails = budgetDetailRepository.existsByBudget(budget);

        if (!hasOtherDetails) {
            budgetRepository.delete(budget);
        }

        // 만원의 행복 검증
        handle_under10000Challenge(member, budget, false ,hasOtherDetails);

    }

    @Transactional
    public Page<BudgetDetailDto> getAllDetails(Long memberId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        LocalDate today = LocalDate.now();

        return budgetDetailRepository
            .findAllBeforeTodayByMemberIdOrderByDateAndCreatedAt(memberId, today, pageable)
            .map(BudgetDetailDto::from);
    }

    @Transactional
    public void registerNoExpense(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        LocalDate today = LocalDate.now();

        Budget budget = budgetRepository.findByMemberAndDate(member, today)
            .orElseGet(() -> {
                Budget newBudget = new Budget();
                newBudget.setMember(member);
                newBudget.setDate(today);
                newBudget.setTotalIncome(BigDecimal.ZERO);
                newBudget.setTotalExpense(BigDecimal.ZERO);
                return budgetRepository.save(newBudget);
            });

    }


    public void handle_under10000Challenge(Member member, Budget budget, boolean empty, boolean hasOtherDetails) {

        LocalDate today = LocalDate.now();
        Challenge challenge = challengeRepository.findByname("만원의 행복")
            .orElseThrow(() -> new RuntimeException("챌린지 정보 없음"));

        // 이미 오늘 생성된 ChallengeCount가 있는지 확인
        Optional<ChallengeCount> existingCount = challengeCountRepository
            .findByMemberAndChallengeAndCreatedAtBetween(
                member,
                challenge,
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
            );

        if (budget.getTotalExpense().compareTo(BigDecimal.valueOf(10000)) <= 0 && !empty && hasOtherDetails)
        {
            if (existingCount.isEmpty()) {
                // 없으면 새로 생성
                ChallengeCount challengeCount = new ChallengeCount();
                challengeCount.setMember(member);
                challengeCount.setCount(1);
                challengeCount.setChallenge(challenge);

                challengeCountRepository.save(challengeCount);
            }
            else
            {
                ChallengeCount count = existingCount.get();
                count.setCount(1);
                challengeCountRepository.save(count);
            }
        }
        else
        {
            if (existingCount.isPresent())
            {
                ChallengeCount count = existingCount.get();
                count.setCount(0);
                challengeCountRepository.save(count);
            }
        }
    }
}
