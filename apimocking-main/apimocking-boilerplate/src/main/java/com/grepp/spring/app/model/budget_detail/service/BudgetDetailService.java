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
import com.grepp.spring.app.model.challenge.service.ChallengeService;
import com.grepp.spring.app.model.challenge_count.domain.ChallengeCount;
import com.grepp.spring.app.model.challenge_count.repos.ChallengeCountRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.infra.error.exceptions.CommonException;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
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
    private final ChallengeService challengeService;

    @Transactional(readOnly = true)
    public BudgetDetailResponseDto findBudgetDetailByDate(String username, String date) {

        Member member = memberRepository.findByEmail(username)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND_MEMBER));

        // 해당 날짜의 Budget이 없을 경우 빈 리스트 반환
        Optional<Budget> optionalBudget = budgetRepository.findByDateWithDetails(LocalDate.parse(date), member);
        if (optionalBudget.isEmpty()) {
            return new BudgetDetailResponseDto(Collections.emptyList());
        }

        Budget budget = optionalBudget.get();

        // Budget에 연결된 BudgetDetail 꺼내기
        List<BudgetDetailDto> details = budget.getBudgetDetails().stream()
            .sorted(Comparator.comparing(BudgetDetail::getCreatedAt).reversed()) // 내림차순
            .map(BudgetDetailDto::from)
            .toList();

        return new BudgetDetailResponseDto(details);
    }

    @Transactional
    public void registerBudgetDetail(String username, BudgetDetailRequestDTO dto) {

        Member member = memberRepository.findByEmail(username)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND_MEMBER));

        // 1. 해당 날짜의 가계부(Budget) 조회
        Budget budget = budgetRepository.findByDateAndMember(LocalDate.parse(dto.getDate()), member)
            .orElseGet(
                () -> budgetRepository.save(Budget.create(LocalDate.parse(dto.getDate()), member)));

        budget.addAmount(dto.getType(), dto.getPrice());

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

        //챌린지
        if (LocalDate.parse(dto.getDate()).equals(LocalDate.now())) {

            //만원의행복
            handle_under10000Challenge(member, budget, false, true);

            //소비단식러
            handle_zerofoodChallenge(member);

            //강철 다리
            handle_zeroTransitionChallenge(member);

            //기록장인
            challengeService.handle_oneMonthAccountChallenge(member);
        }
            //머니 매니저
            challengeService.handle_salaryChallenge(member);
            challengeService.handle_saveMoneyChallenge(member);
    }


    @Transactional
    public UpdatedBudgetDetailResponseDto updateBudgetDetail(Long memberId, Long detailId,
        BudgetDetailRequestDTO dto) {

        // 1. 수정할 BudgetDetail 엔티티 조회
        BudgetDetail budgetDetail = budgetDetailRepository.findById(detailId)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND_DETAIL));

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
                .orElseGet(() -> budgetRepository.save(
                    Budget.create(LocalDate.parse(dto.getDate()), member)));

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
        if (LocalDate.parse(dto.getDate()).equals(LocalDate.now())) {
            handle_under10000Challenge(member, newBudget, false, true);
        } else {
            handle_under10000Challenge(member, oldBudget, empty, true);
        }

        // 소비단식러
        handle_zerofoodChallenge(member);

        // 강철다리
        handle_zeroTransitionChallenge(member);

        // 머니매니저
        challengeService.handle_salaryChallenge(member);

        //기록장인
        challengeService.handle_oneMonthAccountChallenge(member);

        // 절약왕
        challengeService.handle_saveMoneyChallenge(member);

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
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND_MEMBER));

        BudgetDetail detail = budgetDetailRepository.findById(detailId)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND_DETAIL));

        Budget budget = detail.getBudget();
        budget.minusBudgetTotal(detail.getType(), detail.getPrice());

        budgetDetailRepository.delete(detail);

        boolean hasOtherDetails = budgetDetailRepository.existsByBudget(budget);

        if (!hasOtherDetails) {
            budgetRepository.delete(budget);
        }

        // 만원의 행복 검증
        handle_under10000Challenge(member, budget, false, hasOtherDetails);

        // 소비단식러
        handle_zerofoodChallenge(member);

        //강철다리
        handle_zeroTransitionChallenge(member);

        challengeService.handle_salaryChallenge(member);

        //기록장인
        challengeService.handle_oneMonthAccountChallenge(member);

        challengeService.handle_saveMoneyChallenge(member);
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
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND_MEMBER));

        LocalDate today = LocalDate.now();

        Budget budget = budgetRepository.findByMemberAndDate(member, today)
            .orElse(null);

        if (budget != null) {
            if (budget.getTotalExpense().compareTo(BigDecimal.ZERO) == 0) {
                throw new CommonException(ResponseCode.ALREADY_REGISTERED_NO_EXPENSE);
            }

            if (budget.getTotalExpense().compareTo(BigDecimal.ZERO) > 0) {
                throw new CommonException(ResponseCode.ALREADY_REGISTERED_EXPENSE);
            }

            handle_under10000Challenge(member, budget, false, true);
            handle_zerofoodChallenge(member);
            handle_zeroTransitionChallenge(member);
            challengeService.handle_oneMonthAccountChallenge(member);
            challengeService.handle_saveMoneyChallenge(member);
        }

            // 등록
            Budget newBudget = new Budget();
            newBudget.setMember(member);
            newBudget.setDate(today);
            newBudget.setTotalIncome(BigDecimal.ZERO);
            newBudget.setTotalExpense(BigDecimal.ZERO);
            budgetRepository.save(newBudget);

            handle_under10000Challenge(member, newBudget, false, true);
            handle_zerofoodChallenge(member);
            handle_zeroTransitionChallenge(member);
            challengeService.handle_oneMonthAccountChallenge(member);
            challengeService.handle_saveMoneyChallenge(member);

    }


    public void handle_under10000Challenge(Member member, Budget budget, boolean empty,
        boolean hasOtherDetails) {

        LocalDate today = LocalDate.now();
        Challenge challenge = challengeRepository.findByname("만원의 행복")
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND_CHALLENGE));

        // 이미 오늘 생성된 ChallengeCount가 있는지 확인
        Optional<ChallengeCount> existingCount = getChallengeCount(
            member, challenge, today);

        if (budget.getTotalExpense().compareTo(BigDecimal.valueOf(10000)) <= 0 && !empty
            && hasOtherDetails) {
            if (existingCount.isEmpty()) {
                // 없으면 새로 생성
                ChallengeCount challengeCount = new ChallengeCount();
                challengeCount.setMember(member);
                challengeCount.setCount(1);
                challengeCount.setChallenge(challenge);

                challengeCountRepository.save(challengeCount);
            } else {
                ChallengeCount count = existingCount.get();
                count.setCount(1);
                challengeCountRepository.save(count);
            }
        } else {
            if (existingCount.isPresent()) {
                ChallengeCount count = existingCount.get();
                count.setCount(0);
                challengeCountRepository.save(count);
            }
        }
    }


    public void handle_zerofoodChallenge(Member member) {

        LocalDate today = LocalDate.now();
        Challenge challenge = challengeRepository.findByname("소비 단식러")
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND_CHALLENGE));

        Optional<ChallengeCount> existingCount = getChallengeCount(
            member, challenge, today);

        boolean existsByBudget = budgetRepository.existsBudgetByMemberIdAndDate(member.getMemberId(), LocalDate.now());
        boolean  existsByCategory= budgetDetailRepository.existsBudgetDetailByMemberAndDate(member.getMemberId(), "식비", LocalDate.now());

        if (existingCount.isEmpty()) {
            // 없으면 새로 생성
            ChallengeCount challengeCount = new ChallengeCount();
            challengeCount.setMember(member);
            challengeCount.setCount(0);
            challengeCount.setChallenge(challenge);

            challengeCountRepository.save(challengeCount);
            existingCount = Optional.of(challengeCount);
        }

        ChallengeCount count = existingCount.get();
        if(!existsByBudget || existsByCategory)
        {
            count.setCount(0);
        }
        else {
            count.setCount(1);
        }
        challengeCountRepository.save(count);

    }

    public void handle_zeroTransitionChallenge(Member member) {

        LocalDate today = LocalDate.now();
        Challenge challenge = challengeRepository.findByname("강철 다리")
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND_CHALLENGE));

        Optional<ChallengeCount> existingCount = getChallengeCount(
            member, challenge, today);

        boolean existsByBudget = budgetRepository.existsBudgetByMemberIdAndDate(member.getMemberId(), LocalDate.now());
        boolean  existsByCategory= budgetDetailRepository.existsBudgetDetailByMemberAndDate(member.getMemberId(), "교통", LocalDate.now());

        if (existingCount.isEmpty()) {
            // 없으면 새로 생성
            ChallengeCount challengeCount = new ChallengeCount();
            challengeCount.setMember(member);
            challengeCount.setCount(0);
            challengeCount.setChallenge(challenge);

            challengeCountRepository.save(challengeCount);
            existingCount = Optional.of(challengeCount);
        }

        ChallengeCount count = existingCount.get();
        if(!existsByBudget || existsByCategory)
        {
            count.setCount(0);
        }
        else {
            count.setCount(1);
        }
        challengeCountRepository.save(count);

    }

    private Optional<ChallengeCount> getChallengeCount(Member member, Challenge challenge,
        LocalDate today) {
        Optional<ChallengeCount> existingCount = challengeCountRepository
            .findByMemberAndChallengeAndCreatedAtBetween(
                member,
                challenge,
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
            );
        return existingCount;
    }
}

