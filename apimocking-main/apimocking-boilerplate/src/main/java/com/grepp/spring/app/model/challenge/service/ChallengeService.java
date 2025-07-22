package com.grepp.spring.app.model.challenge.service;


import com.grepp.spring.app.model.attendance.repos.AttendanceRepository;
import com.grepp.spring.app.model.budget.repos.BudgetRepository;
import com.grepp.spring.app.model.budget_detail.repos.BudgetDetailRepository;
import com.grepp.spring.app.model.challenge.domain.Challenge;
import com.grepp.spring.app.model.challenge.model.ChallengeStatusDto;
import com.grepp.spring.app.model.challenge.repos.ChallengeRepository;
import com.grepp.spring.app.model.challenge_count.domain.ChallengeCount;
import com.grepp.spring.app.model.challenge_count.repos.ChallengeCountRepository;
import com.grepp.spring.app.model.member.domain.Member;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeCountRepository challengeCountRepository;
    private final AttendanceRepository attendanceRepository;
    private final BudgetDetailRepository budgetDetailRepository;
    private final BudgetRepository budgetRepository;

    @Transactional(readOnly = true)
    public List<ChallengeStatusDto> getChallengeStatuses(Long memberId) {
        LocalDate today = LocalDate.now();
        String currentMonth = YearMonth.now().toString(); // ex: "2025-07"

        return challengeRepository.findWithCount(memberId, today, currentMonth);
    }

    @Transactional
    public void handle_oneMonthChallenge(Member member) {

        LocalDate today = LocalDate.now();

        Challenge challenge = challengeRepository.findByname("개근왕")
            .orElseThrow(() -> new RuntimeException("챌린지 정보 없음"));

        Optional<ChallengeCount> existingCount = getChallengeCount(
            member, challenge, today);

        if (existingCount.isEmpty()) {
            // 없으면 새로 생성
            ChallengeCount challengeCount = new ChallengeCount();
            challengeCount.setMember(member);
            challengeCount.setCount(1);
            challengeCount.setChallenge(challenge);

            challengeCountRepository.save(challengeCount);
            existingCount = Optional.of(challengeCount);
        }

        ChallengeCount count = existingCount.get();

        boolean isAttended = attendanceRepository.existsByMemberAndDate(member, today);

        if (isAttended) {
            // 2. 오늘 challengeCount가 이미 증가했는지 확인
            boolean challengeCountUpdatedToday = challengeCountRepository.existsByMemberAndChallengeAndModifiedAtBetween(
                member,
                challenge,
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
            );
            if (!challengeCountUpdatedToday) {
                // 3. 오늘은 아직 challengeCount 증가 안 했으니 +1
                count.setCount(count.getCount() + 1);
                challengeCountRepository.save(count);
            }
        }

    }

    @Transactional
    public void handle_salaryChallenge(Member member) {

        LocalDate today = LocalDate.now();

        Challenge challenge = challengeRepository.findByname("머니 매니저")
            .orElseThrow(() -> new RuntimeException("챌린지 정보 없음"));

        Optional<ChallengeCount> existingCount = getChallengeCount(
            member, challenge, today);

        boolean existsByBudget = budgetRepository.existsBudgetByMemberIdAndDate(
            member.getMemberId(), LocalDate.now());
        boolean existsByType = budgetDetailRepository.existsTypelByMemberAndDate(
            member.getMemberId(), "수입", LocalDate.now());

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
        if (existsByBudget && existsByType) {
            count.setCount(1);
        } else {
            count.setCount(0);
        }
        challengeCountRepository.save(count);

    }

    @Transactional
    public void handle_oneMonthAccountChallenge(Member member) {

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        Challenge challenge = challengeRepository.findByname("기록 장인")
            .orElseThrow(() -> new RuntimeException("챌린지 정보 없음"));

        // 어제 출석 여부 체크
        boolean attendedYesterday = attendanceRepository.existsByMemberAndDate(member, yesterday);

        // 어제 가계부 작성 여부 체크
        boolean hasBudgetYesterday = budgetRepository.existsByMemberAndDate(member, yesterday);

        Optional<ChallengeCount> existingCount = getChallengeCount(
            member, challenge, today);

        if (existingCount.isEmpty()) {
            // 없으면 새로 생성
            ChallengeCount challengeCount = new ChallengeCount();
            challengeCount.setMember(member);
            challengeCount.setCount(1);
            challengeCount.setChallenge(challenge);

            challengeCountRepository.save(challengeCount);
            existingCount = Optional.of(challengeCount);
        }
        ChallengeCount count = existingCount.get();

        // 오늘 가계부 작성 여부 체크
        boolean hasBudgetToday = budgetRepository.existsByMemberAndDate(member, today);
        if (!hasBudgetToday) {
            return; // 오늘 가계부 안 썼으면 아무것도 안 함
        }

        // 오늘 이미 count 처리했는지 확인 (중복 증가 방지)
        if (count.getModifiedAt() != null && count.getModifiedAt().toLocalDate().isEqual(today)) {
            return; // 오늘 이미 처리된 상태
        }

        if (attendedYesterday && hasBudgetYesterday) {
            count.setCount(count.getCount() + 1); // 연속 작성 인정
        } else {
            count.setCount(1); // 연속 안 됨 → 처음부터 시작
        }

        count.setModifiedAt(LocalDateTime.now());
        challengeCountRepository.save(count);
    }

    @Transactional
    public void handle_saveMoneyChallenge(Member member) {

        LocalDate today = LocalDate.now();

        Challenge challenge = challengeRepository.findByname("절약왕")
            .orElseThrow(() -> new RuntimeException("챌린지 정보 없음"));

        Optional<ChallengeCount> existingCount = getChallengeCount(
            member, challenge, today);

        if (existingCount.isEmpty()) {
            // 없으면 새로 생성
            ChallengeCount challengeCount = new ChallengeCount();
            challengeCount.setMember(member);
            challengeCount.setCount(1);
            challengeCount.setChallenge(challenge);

            challengeCountRepository.save(challengeCount);
            existingCount = Optional.of(challengeCount);
        }
        ChallengeCount count = existingCount.get();


        YearMonth thisMonth = YearMonth.from(today);
        YearMonth lastMonth = thisMonth.minusMonths(1);

        // 7.1
        LocalDate thisMonthStart = thisMonth.atDay(1);

        // 6.1~6.30
        LocalDate lastMonthStart = lastMonth.atDay(1);
        LocalDate lastMonthEnd = lastMonth.atEndOfMonth();

        // BudgetDetail 기준 합산 (오늘까지)
        BigDecimal thisMonthSum = budgetRepository.sumExpenseByMemberAndDateBetween(
            member.getMemberId(), thisMonthStart, today
        );

        BigDecimal lastMonthSum = budgetRepository.sumExpenseByMemberAndDateBetween(
            member.getMemberId(), lastMonthStart, lastMonthEnd
        );

        if(lastMonthSum.compareTo(thisMonthSum) < 0) {
            count.setCount(0);
        }
        else {
            count.setCount(1);
        }

    }


    private Optional<ChallengeCount> getChallengeCount(Member member, Challenge challenge,
        LocalDate today) {
        Optional<ChallengeCount> existingCount = challengeCountRepository
            .findByMemberAndChallengeAndCreatedAtBetween(
                member,
                challenge,
                today.withDayOfMonth(1).atStartOfDay(),
                today.withDayOfMonth(1).plusMonths(1).atStartOfDay()
            );
        return existingCount;
    }
}
