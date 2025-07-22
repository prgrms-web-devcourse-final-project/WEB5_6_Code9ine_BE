package com.grepp.spring.app.model.challenge.service;


import com.grepp.spring.app.model.achieved_title.domain.AchievedTitle;
import com.grepp.spring.app.model.achieved_title.repos.AchievedTitleRepository;
import com.grepp.spring.app.model.attendance.repos.AttendanceRepository;
import com.grepp.spring.app.model.budget.repos.BudgetRepository;
import com.grepp.spring.app.model.budget_detail.repos.BudgetDetailRepository;
import com.grepp.spring.app.model.challenge.domain.Challenge;
import com.grepp.spring.app.model.challenge.model.ChallengeStatusDto;
import com.grepp.spring.app.model.challenge.repos.ChallengeRepository;
import com.grepp.spring.app.model.challenge_count.domain.ChallengeCount;
import com.grepp.spring.app.model.challenge_count.repos.ChallengeCountRepository;
import com.grepp.spring.app.model.community.domain.CommunityLike;
import com.grepp.spring.app.model.community.repos.CommunityCommentRepository;
import com.grepp.spring.app.model.community.repos.CommunityLikeRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.app.model.notification.repos.NotificationRepository;
import com.grepp.spring.app.model.notification.service.NotificationService;
import com.grepp.spring.app.model.notification.service.NotificationService.NotificationCreateRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final AchievedTitleRepository achievedTitleRepository;
    private final CommunityLikeRepository communityLikeRepository;
    private final CommunityCommentRepository communityCommentRepository;

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

        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);

//        boolean existsByBudget = budgetRepository.existsBudgetByMemberIdAndDate(
//            member.getMemberId(), LocalDate.now());
        boolean existsByType = budgetDetailRepository.existsByTypeInMonth(
            member.getMemberId(), "수입", startOfMonth.toLocalDate(), endOfMonth.toLocalDate());

        boolean notification = notificationRepository.existsMonthlyNotification(member.getMemberId(), startOfMonth,
            endOfMonth,"머니 매니저 칭호를 획득했어요!");

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
        System.out.print(existsByType);
        if (existsByType) {
            count.setCount(1);
            if(!notification)
            {
                createdAchievedTitle(member,count);
                createNotification(member, count);
            }
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
        if (lastMonthSum == null) {
            lastMonthSum = BigDecimal.ZERO;
        }
        if (lastMonthSum.compareTo(thisMonthSum) < 0) {
            count.setCount(0);
        } else {
            count.setCount(1);
        }

    }

    @Transactional
    public void handle_heartChallenge(Member member) {

        LocalDate today = LocalDate.now();

        Challenge challenge = challengeRepository.findByname("소통왕")
            .orElseThrow(() -> new RuntimeException("챌린지 정보 없음"));

        Optional<ChallengeCount> existingCount = getChallengeCount(
            member, challenge, today);

        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);

        int commentcount = communityCommentRepository.countAchievedComment(member.getMemberId(), startOfMonth, endOfMonth);
        int commentlike = communityLikeRepository.countAchievedLike(member.getMemberId(), startOfMonth, endOfMonth);

        if(commentcount>5)
            commentcount=5;

        if(commentlike>5)
            commentlike=5;

        boolean notification = notificationRepository.existsMonthlyNotification(member.getMemberId(), startOfMonth,
            endOfMonth,"소통왕 칭호를 획득했어요!");

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

        count.setCount(commentcount+commentlike);

        if(!notification)
        {
            if(count.getCount()>=10)
            {
                createdAchievedTitle(member,count);
                createNotification(member, count);
            }
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

    @Scheduled(cron = "0 0 0 * * *") // 매일 00시(자정)에 실행
    //@Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul") // 매 분 0초마다 실행
    public void daily_notifyChallengeSuccess() {
        LocalDateTime startOfYesterday = LocalDate.now().minusDays(1).atStartOfDay(); // 어제 00:00:00
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay(); // 오늘 00:00:00

        List<Member> allMembers = memberRepository.findAll(); // 전체 회원 조회

        for (Member member : allMembers) {
            List<ChallengeCount> counts = challengeCountRepository.findDailyChallenges(
                member.getMemberId(), startOfYesterday, startOfToday, "일일");

            System.out.println("알림시작");
            for (ChallengeCount cc : counts) {
                if (cc.getCount() == cc.getChallenge().getTotal()) {

                    createdAchievedTitle(member,cc);
                    createNotification(member, cc);
                    System.out.println("✅ 챌린지 " + cc.getChallenge().getName() + " 성공");
                } else {
                    System.out.println("❌ 챌린지 " + cc.getChallenge().getName() + " 실패");
                }
            }
        }
    }

     @Scheduled(cron = "0 0 0 1 * *") // 매달 1일에 실행
    //@Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul") // 매 분 실행
    public void monthly_notifyChallengeSuccess() {

        LocalDateTime startOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1).atStartOfDay(); // 7월 1일 00:00:00
        LocalDateTime endOfLastMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        List<Member> allMembers = memberRepository.findAll(); // 전체 회원 조회

        for (Member member : allMembers) {
            List<ChallengeCount> counts = challengeCountRepository.findDailyChallenges(
                member.getMemberId(), startOfLastMonth, endOfLastMonth,"월간");

            boolean notification = notificationRepository.existsMonthlyNotification(member.getMemberId(), startOfLastMonth,
                endOfLastMonth,"머니 매니저 칭호를 획득했어요!");

            boolean notification_heart = notificationRepository.existsMonthlyNotification(member.getMemberId(), startOfLastMonth,
                endOfLastMonth,"소통왕 칭호를 획득했어요!");

            System.out.println("알림시작");
            for (ChallengeCount cc : counts) {
                if (cc.getCount() == cc.getChallenge().getTotal()) {

                    if (cc.getChallenge().getName().equals("머니 매니저")) {
                        if (!notification) {
                            createdAchievedTitle(member, cc);
                            createNotification(member, cc);
                        }
                    }
                    else if(cc.getChallenge().getName().equals("소통왕"))
                    {
                        if (!notification_heart) {
                            createdAchievedTitle(member, cc);
                            createNotification(member, cc);
                        }
                    }
                    else {
                        createdAchievedTitle(member, cc);
                        createNotification(member, cc);
                    }
                    System.out.println("✅ 챌린지 " + cc.getChallenge().getName() + " 성공");
                } else {
                    System.out.println("❌ 챌린지 " + cc.getChallenge().getName() + " 실패");
                }
            }
        }
    }

    public void createdAchievedTitle(Member member,ChallengeCount cc) {
        Optional<AchievedTitle> optional = achievedTitleRepository.findByMemberAndName(member,cc.getChallenge().getName());

        if (optional.isPresent()) {
            AchievedTitle existing = optional.get();
            existing.setMinCount(existing.getMinCount() + 1); // count 증가
            achievedTitleRepository.save(existing);
        } else {
            AchievedTitle newTitle = new AchievedTitle();
            newTitle.setName(cc.getChallenge().getName());
            newTitle.setChallenge(cc.getChallenge());
            newTitle.setAchieved(false);
            newTitle.setMember(member);
            newTitle.setIcon(cc.getChallenge().getIcon());
            newTitle.setMinCount(1); // 처음은 1로 설정
            achievedTitleRepository.save(newTitle);
        }
    }

    private void createNotification(Member member, ChallengeCount cc) {
        NotificationCreateRequest request = new NotificationCreateRequest(
            member.getMemberId(),
            0L,
            "TITLE",
            "",
            "운영자",
            cc.getChallenge().getName());
        notificationService.createNotification(request);
    }


}
