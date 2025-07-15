package com.grepp.spring.app.model.challenge_count.service;

import com.grepp.spring.app.model.challenge_count.domain.ChallengeCount;
import com.grepp.spring.app.model.challenge_count.model.ChallengeTopResponse;
import com.grepp.spring.app.model.challenge_count.repos.ChallengeCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainPageChallengeCountService {

    private final ChallengeCountRepository challengeCountRepository;

    public List<ChallengeTopResponse> findTopChallengeCounts() {
        Pageable pageable = PageRequest.of(0, 3);
        return challengeCountRepository.findTopAchievedChallengeNames(pageable);
    }
}