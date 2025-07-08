package com.grepp.spring.app.model.challenge_count.repos;

import com.grepp.spring.app.model.challenge.domain.Challenge;
import com.grepp.spring.app.model.challenge_count.domain.ChallengeCount;
import com.grepp.spring.app.model.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChallengeCountRepository extends JpaRepository<ChallengeCount, Long> {

    ChallengeCount findFirstByMember(Member member);

    ChallengeCount findFirstByChallenge(Challenge challenge);

}
