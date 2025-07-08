package com.grepp.spring.app.model.challenge.repos;

import com.grepp.spring.app.model.challenge.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
}
