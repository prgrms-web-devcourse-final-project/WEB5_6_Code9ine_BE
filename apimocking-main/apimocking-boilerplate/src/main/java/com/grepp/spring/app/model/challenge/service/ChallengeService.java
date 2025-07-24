package com.grepp.spring.app.model.challenge.service;


import com.grepp.spring.app.model.achieved_title.domain.AchievedTitle;
import com.grepp.spring.app.model.achieved_title.repos.AchievedTitleRepository;
import com.grepp.spring.app.model.attendance.repos.AttendanceRepository;
import com.grepp.spring.app.model.budget.repos.BudgetRepository;
import com.grepp.spring.app.model.budget_detail.repos.BudgetDetailRepository;
import com.grepp.spring.app.model.challenge.code.ChallengeCategory;
import com.grepp.spring.app.model.challenge.code.CommunityCategory;
import com.grepp.spring.app.model.challenge.domain.Challenge;
import com.grepp.spring.app.model.challenge.model.ChallengeStatusDto;
import com.grepp.spring.app.model.challenge.repos.ChallengeRepository;
import com.grepp.spring.app.model.challenge_count.domain.ChallengeCount;
import com.grepp.spring.app.model.challenge_count.repos.ChallengeCountRepository;
import com.grepp.spring.app.model.community.domain.CommunityLike;
import com.grepp.spring.app.model.community.repos.CommunityCommentRepository;
import com.grepp.spring.app.model.community.repos.CommunityLikeRepository;
import com.grepp.spring.app.model.challenge_history.domain.ChallengeHistory;
import com.grepp.spring.app.model.challenge_history.repository.ChallengeHistoryRepository;
import com.grepp.spring.app.model.community.domain.CommunityPost;
import com.grepp.spring.app.model.community.repos.CommunityRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.app.model.notification.repos.NotificationRepository;
import com.grepp.spring.app.model.notification.service.NotificationService;
import com.grepp.spring.app.model.notification.service.NotificationService.NotificationCreateRequest;
import com.grepp.spring.util.NotFoundException;
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
    private final ChallengeHistoryRepository challengeHistoryRepository;
    private final CommunityRepository communityRepository;

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
                member.setTotalExp(member.getTotalExp() + 100);
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
                member.setTotalExp(member.getTotalExp() + 100);
                memberRepository.save(member);
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
                    member.setTotalExp(member.getTotalExp() + 20);
                    memberRepository.save(member);
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
                            member.setTotalExp(member.getTotalExp() + 100);
                            memberRepository.save(member);
                        }
                    }
                    else if(cc.getChallenge().getName().equals("소통왕"))
                    {
                        if (!notification_heart) {
                            createdAchievedTitle(member, cc);
                            createNotification(member, cc);
                            member.setTotalExp(member.getTotalExp() + 100);
                            memberRepository.save(member);
                        }
                    }
                    else {
                        createdAchievedTitle(member, cc);
                        createNotification(member, cc);
                        member.setTotalExp(member.getTotalExp() + 100);
                        memberRepository.save(member);
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



    // 제로 마스터, 노노카페, 냉털 요리왕, 착한 가게 방문 인증 챌린지 달성 여부 확인
    @Transactional
    public void checkChallenge(CommunityPost post) {

        // 좋아요 5개 미만 시 실패
        if (post.getLikeCount() < 5) return;

        // 이미지가 없을 시 실패
        if (post.getImages() == null || post.getImages().isEmpty()) return;

        // 커뮤니티 카테고리가 챌린지가 아닐 시 실패
        ChallengeCategory challengeCategory = post.getChallenge();
        if (challengeCategory == null) return;

        // 해당 카테고리에만 해당 되도록
        if (!(challengeCategory == ChallengeCategory.NO_MONEY ||
            challengeCategory == ChallengeCategory.MASTER ||
            challengeCategory == ChallengeCategory.COOK_KING ||
            challengeCategory == ChallengeCategory.KIND_CONSUMER)) {
            return;
        }

        Member postWriter = post.getMember();

        // 존재하는 챌린지인지 조회
        Challenge challenge = challengeRepository.findByName(mapCategoryToName(challengeCategory))
            .orElseThrow(() -> new NotFoundException("해당 챌린지를 찾을 수 없습니다"));

        // 이미 달성한 챌린지인지 확인
        boolean alreadyAchievedHistory = challengeHistoryRepository.existsByPostAndMember(post, postWriter);
        boolean alreadyAchievedTitle = achievedTitleRepository.existsByMemberAndChallenge(postWriter, challenge);

        if (alreadyAchievedHistory || alreadyAchievedTitle) return;

        // 챌린지 기록 저장
        ChallengeHistory history = new ChallengeHistory();
        history.setMember(postWriter);
        history.setPost(post);
        history.setChallenge(challenge);
        challengeHistoryRepository.save(history);

        // 챌린지 횟수 저장
        ChallengeCount count = new ChallengeCount();
        count.setMember(postWriter);
        count.setChallenge(challenge);
        count.setCount(1);
        challengeCountRepository.save(count);

        // 경험치 추가
        postWriter.setTotalExp(postWriter.getTotalExp() + 100);

        // 알림 전송
        notificationService.createNotification(new NotificationService.NotificationCreateRequest(
            postWriter.getMemberId(),
            0L,
            "TITLE",
            null,
            null,
            challenge.getName()
        ));

        // 획득한 칭호 저장
        createAchievedTitle(postWriter, challenge);
    }

    // 숨은 맛집 탐방 챌린지 달성 여부 확인 메서드
    @Transactional
    public void checkMyStoreChallenge(CommunityPost post) {

        // 카테고리가 숨맛탐이 아닐 시 실패
        if (post.getCategory() != CommunityCategory.MY_STORE) return;

        Member postWriter = post.getMember();

        // 로그인한 사용자가 작성한 숨맛탐 게시글 개수
        int count = communityRepository.countByMemberAndCategory(postWriter, CommunityCategory.MY_STORE);

        // 존재하는 챌린지인지 조회
        Challenge challenge = challengeRepository.findByName("숨.맛.탐")
            .orElseThrow(() -> new NotFoundException("해당 챌린지를 찾을 수 없습니다"));

        // 이미 달성한 챌린지인지 확인
        if (challengeHistoryRepository.existsByMemberAndChallenge(postWriter, challenge)) return;

        // ChallengeCount 테이블에 없는 경우 생성
        ChallengeCount challengeCount = challengeCountRepository
            .findByMemberAndChallenge(postWriter, challenge)
            .orElseGet(() -> {
                ChallengeCount cc = new ChallengeCount();
                cc.setMember(postWriter);
                cc.setChallenge(challenge);
                cc.setCount(0);
                return cc;
            });

        challengeCount.setCount(Math.min(count, 5));
        challengeCountRepository.save(challengeCount);

        // 게시글 개수가 5개 이상일 경우 종료
        if (challengeCount.getCount() > 5) return;

        // 게시글 개수가 5개일 경우
        if (challengeCount.getCount() == 5) {

            // 챌린지 기록 저장
            ChallengeHistory history = new ChallengeHistory();
            history.setMember(postWriter);
            history.setPost(post);
            history.setChallenge(challenge);
            challengeHistoryRepository.save(history);

            // 경험치 추가
            postWriter.setTotalExp(postWriter.getTotalExp() + 100);

            // 알림 전송
            notificationService.createNotification(new NotificationService.NotificationCreateRequest(
                postWriter.getMemberId(),
                0L,
                "TITLE",
                null,
                null,
                challenge.getName()
            ));

            // 획득한 칭호 저장
            createAchievedTitle(postWriter, challenge);
        }
    }

    // achieved_title 테이블 데이터 삽입
    private void createAchievedTitle(Member member, Challenge challenge) {
        boolean alreadyExists = achievedTitleRepository.existsByMemberAndChallenge(member, challenge);
        if (alreadyExists) return;

        AchievedTitle achievedTitle = new AchievedTitle();
        achievedTitle.setName(challenge.getName()); // 칭호 이름
        achievedTitle.setAchieved(true);
        achievedTitle.setMinCount(1); // 커뮤니티는 단발성이므로 1로 설정
        achievedTitle.setChallenge(challenge);
        achievedTitle.setMember(member);
        achievedTitle.setIcon(challenge.getIcon());

        achievedTitleRepository.save(achievedTitle);
    }

    // 챌린지 카테고리 이름 맵핑
    private String mapCategoryToName(ChallengeCategory category) {
        return switch (category) {
            case NO_MONEY -> "제로 마스터";
            case MASTER -> "노노 카페";
            case COOK_KING -> "냉털 요리왕";
            case KIND_CONSUMER -> "착한 소비러";
            case DETECTIVE -> "숨.맛.탐";
        };
    }
}
