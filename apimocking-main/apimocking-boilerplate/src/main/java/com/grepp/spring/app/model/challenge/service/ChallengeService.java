package com.grepp.spring.app.model.challenge.service;

import com.grepp.spring.app.model.achieved_title.domain.AchievedTitle;
import com.grepp.spring.app.model.achieved_title.repos.AchievedTitleRepository;
import com.grepp.spring.app.model.challenge.domain.Challenge;
import com.grepp.spring.app.model.challenge.model.ChallengeDTO;
import com.grepp.spring.app.model.challenge.repos.ChallengeRepository;
import com.grepp.spring.app.model.challenge_count.domain.ChallengeCount;
import com.grepp.spring.app.model.challenge_count.repos.ChallengeCountRepository;
import com.grepp.spring.util.NotFoundException;
import com.grepp.spring.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeCountRepository challengeCountRepository;
    private final AchievedTitleRepository achievedTitleRepository;

    public ChallengeService(final ChallengeRepository challengeRepository,
            final ChallengeCountRepository challengeCountRepository,
            final AchievedTitleRepository achievedTitleRepository) {
        this.challengeRepository = challengeRepository;
        this.challengeCountRepository = challengeCountRepository;
        this.achievedTitleRepository = achievedTitleRepository;
    }

    public List<ChallengeDTO> findAll() {
        final List<Challenge> challenges = challengeRepository.findAll(Sort.by("challengeId"));
        return challenges.stream()
                .map(challenge -> mapToDTO(challenge, new ChallengeDTO()))
                .toList();
    }

    public ChallengeDTO get(final Long challengeId) {
        return challengeRepository.findById(challengeId)
                .map(challenge -> mapToDTO(challenge, new ChallengeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ChallengeDTO challengeDTO) {
        final Challenge challenge = new Challenge();
        mapToEntity(challengeDTO, challenge);
        return challengeRepository.save(challenge).getChallengeId();
    }

    public void update(final Long challengeId, final ChallengeDTO challengeDTO) {
        final Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(challengeDTO, challenge);
        challengeRepository.save(challenge);
    }

    public void delete(final Long challengeId) {
        challengeRepository.deleteById(challengeId);
    }

    private ChallengeDTO mapToDTO(final Challenge challenge, final ChallengeDTO challengeDTO) {
        challengeDTO.setChallengeId(challenge.getChallengeId());
        challengeDTO.setName(challenge.getName());
        challengeDTO.setDescription(challenge.getDescription());
        challengeDTO.setType(challenge.getType());
        challengeDTO.setExp(challenge.getExp());
        return challengeDTO;
    }

    private Challenge mapToEntity(final ChallengeDTO challengeDTO, final Challenge challenge) {
        challenge.setName(challengeDTO.getName());
        challenge.setDescription(challengeDTO.getDescription());
        challenge.setType(challengeDTO.getType());
        challenge.setExp(challengeDTO.getExp());
        return challenge;
    }

    public ReferencedWarning getReferencedWarning(final Long challengeId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(NotFoundException::new);
        final ChallengeCount challengeChallengeCount = challengeCountRepository.findFirstByChallenge(challenge);
        if (challengeChallengeCount != null) {
            referencedWarning.setKey("challenge.challengeCount.challenge.referenced");
            referencedWarning.addParam(challengeChallengeCount.getChallengeCountId());
            return referencedWarning;
        }
        final AchievedTitle challengeAchievedTitle = achievedTitleRepository.findFirstByChallenge(challenge);
        if (challengeAchievedTitle != null) {
            referencedWarning.setKey("challenge.achievedTitle.challenge.referenced");
            referencedWarning.addParam(challengeAchievedTitle.getATId());
            return referencedWarning;
        }
        return null;
    }

}
