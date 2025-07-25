package com.grepp.spring.app.model.achieved_title.repos;

import com.grepp.spring.app.model.achieved_title.domain.AchievedTitle;
import com.grepp.spring.app.model.achieved_title.model.AchievedTitleDTO;
import com.grepp.spring.app.model.challenge.domain.Challenge;
import com.grepp.spring.app.model.member.domain.Member;
import feign.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface AchievedTitleRepository extends JpaRepository<AchievedTitle, Long> {

    AchievedTitle findFirstByChallenge(Challenge challenge);

    Optional<AchievedTitle> findByMemberAndName(Member member, String name);

    boolean existsByMemberAndChallenge(Member member, Challenge challenge);


    @Query("SELECT new com.grepp.spring.app.model.achieved_title.model.AchievedTitleDTO(a.challenge.challengeId, a.name, a.minCount,a.icon) " +
        "FROM AchievedTitle a WHERE a.member.memberId = :memberId")
    List<AchievedTitleDTO> findDtoByMemberId(@Param("memberId") Long memberId);
}
