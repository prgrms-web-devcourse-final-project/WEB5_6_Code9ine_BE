package com.grepp.spring.app.model.member.repos;

import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.model.TopSaversResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmailIgnoreCase(String email);
    java.util.Optional<Member> findByEmail(String email);


    @Query("""
    SELECT new com.grepp.spring.app.model.member.model.TopSaversResponse(
        m.memberId, m.nickname, m.level, m.profileImage, at.name
    )
    FROM AchievedTitle at
    JOIN at.member m
    ORDER BY m.level DESC
""")
    List<TopSaversResponse> getTopSavers(Pageable pageable);
}
