package com.grepp.spring.app.model.challenge.service;


import com.grepp.spring.app.model.challenge.domain.Challenge;
import com.grepp.spring.app.model.challenge.model.ChallengeStatusDto;
import com.grepp.spring.app.model.challenge.repos.ChallengeRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    @Transactional(readOnly = true)
    public List<ChallengeStatusDto> getChallengeStatuses(Long memberId) {
        LocalDate today = LocalDate.now();
        String currentMonth = YearMonth.now().toString(); // ex: "2025-07"

        return challengeRepository.findWithCount(memberId, today, currentMonth);
    }
}
