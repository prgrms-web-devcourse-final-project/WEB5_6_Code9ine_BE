package com.grepp.spring.app.model.challenge_count.repos;

import com.grepp.spring.app.model.challenge.domain.Challenge;
import com.grepp.spring.app.model.challenge_count.domain.ChallengeCount;
import com.grepp.spring.app.model.challenge_count.model.ChallengeTopResponse;
import com.grepp.spring.app.model.member.domain.Member;
import feign.Param;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ChallengeCountRepository extends JpaRepository<ChallengeCount, Long> {

    ChallengeCount findFirstByMember(Member member);

    ChallengeCount findFirstByChallenge(Challenge challenge);

    @Query("""
        SELECT new com.grepp.spring.app.model.challenge_count.model.ChallengeTopResponse(
            c.description
        )
        FROM ChallengeCount cc
        JOIN cc.challenge c
        GROUP BY c.challengeId, c.name
        ORDER BY SUM(cc.count) DESC
    """)
    List<ChallengeTopResponse> findTopAchievedChallengeNames(Pageable pageable);


    Optional<ChallengeCount> findByMemberAndChallengeAndCreatedAtBetween(Member member, Challenge challenge, LocalDateTime localDateTime, LocalDateTime localDateTime1);

    boolean existsByMemberAndChallengeAndModifiedAtBetween(Member member, Challenge challenge, LocalDateTime localDateTime, LocalDateTime localDateTime1);

    Optional<ChallengeCount> findByMemberAndChallenge(Member member, Challenge challenge);

    @Query("SELECT cc FROM ChallengeCount cc " +
        "JOIN FETCH cc.challenge c " +
        "WHERE cc.member.memberId = :memberId " +
        "AND cc.createdAt >= :startOfYesterday " +
        "AND cc.createdAt < :startOfToday " +
        "AND cc.challenge.type= :type")
    List<ChallengeCount> findDailyChallenges(
        @Param("memberId") Long memberId,
        @Param("startOfYesterday") LocalDateTime startOfYesterday,
        @Param("startOfToday") LocalDateTime startOfToday,
        @Param("type") String type);

    // 회원 탈퇴 시 해당 회원의 모든 챌린지 카운트 삭제
    @Query("DELETE FROM ChallengeCount cc WHERE cc.member = :member")
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    void deleteByMember(@Param("member") Member member);
}
