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

        if(isAttended) {
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

        boolean existsByBudget = budgetRepository.existsBudgetByMemberIdAndDate(member.getMemberId(), LocalDate.now());
        boolean  existsByType= budgetDetailRepository.existsTypelByMemberAndDate(member.getMemberId(), "수입", LocalDate.now());

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
        if(existsByBudget && existsByType)
        {
            count.setCount(1);
        }
        else {
            count.setCount(0);
        }
        challengeCountRepository.save(count);

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
