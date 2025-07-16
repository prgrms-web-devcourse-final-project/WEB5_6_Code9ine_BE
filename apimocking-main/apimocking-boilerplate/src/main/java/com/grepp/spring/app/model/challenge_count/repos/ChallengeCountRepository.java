package com.grepp.spring.app.model.challenge_count.repos;

import com.grepp.spring.app.model.challenge.domain.Challenge;
import com.grepp.spring.app.model.challenge_count.domain.ChallengeCount;
import com.grepp.spring.app.model.challenge_count.model.ChallengeTopResponse;
import com.grepp.spring.app.model.member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ChallengeCountRepository extends JpaRepository<ChallengeCount, Long> {

    ChallengeCount findFirstByMember(Member member);

    ChallengeCount findFirstByChallenge(Challenge challenge);

    @Query("""
        SELECT new com.grepp.spring.app.model.challenge_count.model.ChallengeTopResponse(
            c.name
        )
        FROM ChallengeCount cc
        JOIN cc.challenge c
        GROUP BY c.challengeId, c.name
        ORDER BY SUM(cc.count) DESC
    """)
    List<ChallengeTopResponse> findTopAchievedChallengeNames(Pageable pageable);
}
