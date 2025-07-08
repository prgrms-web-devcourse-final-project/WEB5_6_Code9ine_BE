package com.grepp.spring.app.model.achieved_title.repos;

import com.grepp.spring.app.model.achieved_title.domain.AchievedTitle;
import com.grepp.spring.app.model.challenge.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AchievedTitleRepository extends JpaRepository<AchievedTitle, Long> {

    AchievedTitle findFirstByChallenge(Challenge challenge);

}
