package com.grepp.spring.app.model.challenge_history.repository;

import com.grepp.spring.app.model.challenge.domain.Challenge;
import com.grepp.spring.app.model.challenge_history.domain.ChallengeHistory;
import com.grepp.spring.app.model.community.domain.CommunityPost;
import com.grepp.spring.app.model.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeHistoryRepository extends JpaRepository<ChallengeHistory, Long> {

    boolean existsByPostAndMember(CommunityPost post, Member member);

    boolean existsByMemberAndChallenge(Member member, Challenge challenge);
}
