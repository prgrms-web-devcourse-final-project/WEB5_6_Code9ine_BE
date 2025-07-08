package com.grepp.spring.app.model.achieved_title.service;

import com.grepp.spring.app.model.achieved_title.domain.AchievedTitle;
import com.grepp.spring.app.model.achieved_title.model.AchievedTitleDTO;
import com.grepp.spring.app.model.achieved_title.repos.AchievedTitleRepository;
import com.grepp.spring.app.model.challenge.domain.Challenge;
import com.grepp.spring.app.model.challenge.repos.ChallengeRepository;
import com.grepp.spring.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class AchievedTitleService {

    private final AchievedTitleRepository achievedTitleRepository;
    private final ChallengeRepository challengeRepository;

    public AchievedTitleService(final AchievedTitleRepository achievedTitleRepository,
            final ChallengeRepository challengeRepository) {
        this.achievedTitleRepository = achievedTitleRepository;
        this.challengeRepository = challengeRepository;
    }

    public List<AchievedTitleDTO> findAll() {
        final List<AchievedTitle> achievedTitles = achievedTitleRepository.findAll(Sort.by("aTId"));
        return achievedTitles.stream()
                .map(achievedTitle -> mapToDTO(achievedTitle, new AchievedTitleDTO()))
                .toList();
    }

    public AchievedTitleDTO get(final Long aTId) {
        return achievedTitleRepository.findById(aTId)
                .map(achievedTitle -> mapToDTO(achievedTitle, new AchievedTitleDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final AchievedTitleDTO achievedTitleDTO) {
        final AchievedTitle achievedTitle = new AchievedTitle();
        mapToEntity(achievedTitleDTO, achievedTitle);
        return achievedTitleRepository.save(achievedTitle).getATId();
    }

    public void update(final Long aTId, final AchievedTitleDTO achievedTitleDTO) {
        final AchievedTitle achievedTitle = achievedTitleRepository.findById(aTId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(achievedTitleDTO, achievedTitle);
        achievedTitleRepository.save(achievedTitle);
    }

    public void delete(final Long aTId) {
        achievedTitleRepository.deleteById(aTId);
    }

    private AchievedTitleDTO mapToDTO(final AchievedTitle achievedTitle,
            final AchievedTitleDTO achievedTitleDTO) {
        achievedTitleDTO.setATId(achievedTitle.getATId());
        achievedTitleDTO.setName(achievedTitle.getName());
        achievedTitleDTO.setAchieved(achievedTitle.getAchieved());
        achievedTitleDTO.setMinCount(achievedTitle.getMinCount());
        achievedTitleDTO.setChallenge(achievedTitle.getChallenge() == null ? null : achievedTitle.getChallenge().getChallengeId());
        return achievedTitleDTO;
    }

    private AchievedTitle mapToEntity(final AchievedTitleDTO achievedTitleDTO,
            final AchievedTitle achievedTitle) {
        achievedTitle.setName(achievedTitleDTO.getName());
        achievedTitle.setAchieved(achievedTitleDTO.getAchieved());
        achievedTitle.setMinCount(achievedTitleDTO.getMinCount());
        final Challenge challenge = achievedTitleDTO.getChallenge() == null ? null : challengeRepository.findById(achievedTitleDTO.getChallenge())
                .orElseThrow(() -> new NotFoundException("challenge not found"));
        achievedTitle.setChallenge(challenge);
        return achievedTitle;
    }

}
