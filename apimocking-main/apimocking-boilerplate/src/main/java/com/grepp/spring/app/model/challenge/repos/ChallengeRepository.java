package com.grepp.spring.app.model.challenge.repos;

import com.grepp.spring.app.model.challenge.domain.Challenge;
import com.grepp.spring.app.model.challenge.model.ChallengeStatusDto;
import feign.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ChallengeRepository extends JpaRepository<Challenge, Long> {


    @Query("""
    SELECT new com.grepp.spring.app.model.challenge.model.ChallengeStatusDto(
        c.challengeId,
        c.name,
        c.description,
        c.type,
        c.total,
        COALESCE(cc.count, 0),
        c.icon
    )
    FROM Challenge c
    LEFT JOIN ChallengeCount cc ON cc.challenge = c AND cc.member.memberId = :memberId
        AND (
            (c.type = '일일' AND FUNCTION('DATE', cc.createdAt) = :today)
            OR (c.type = '월간' AND FUNCTION('TO_CHAR', cc.createdAt, 'YYYY-MM') = :currentMonth)
            OR (c.type = '커뮤니티')
        )
    ORDER BY c.challengeId
""")
    List<ChallengeStatusDto> findWithCount(@Param("memberId") Long memberId,
        @Param("today") LocalDate today,
        @Param("currentMonth") String currentMonth);

    Optional<Challenge> findByname(String name);
}
