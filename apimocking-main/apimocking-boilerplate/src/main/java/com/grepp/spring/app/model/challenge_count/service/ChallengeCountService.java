package com.grepp.spring.app.model.challenge_count.service;

import com.grepp.spring.app.model.challenge.domain.Challenge;
import com.grepp.spring.app.model.challenge.repos.ChallengeRepository;
import com.grepp.spring.app.model.challenge_count.domain.ChallengeCount;
import com.grepp.spring.app.model.challenge_count.model.ChallengeCountDTO;
import com.grepp.spring.app.model.challenge_count.repos.ChallengeCountRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ChallengeCountService {

    private final ChallengeCountRepository challengeCountRepository;
    private final MemberRepository memberRepository;
    private final ChallengeRepository challengeRepository;

    public ChallengeCountService(final ChallengeCountRepository challengeCountRepository,
            final MemberRepository memberRepository,
            final ChallengeRepository challengeRepository) {
        this.challengeCountRepository = challengeCountRepository;
        this.memberRepository = memberRepository;
        this.challengeRepository = challengeRepository;
    }

    public List<ChallengeCountDTO> findAll() {
        final List<ChallengeCount> challengeCounts = challengeCountRepository.findAll(Sort.by("challengeCountId"));
        return challengeCounts.stream()
                .map(challengeCount -> mapToDTO(challengeCount, new ChallengeCountDTO()))
                .toList();
    }

    public ChallengeCountDTO get(final Long challengeCountId) {
        return challengeCountRepository.findById(challengeCountId)
                .map(challengeCount -> mapToDTO(challengeCount, new ChallengeCountDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ChallengeCountDTO challengeCountDTO) {
        final ChallengeCount challengeCount = new ChallengeCount();
        mapToEntity(challengeCountDTO, challengeCount);
        return challengeCountRepository.save(challengeCount).getChallengeCountId();
    }

    public void update(final Long challengeCountId, final ChallengeCountDTO challengeCountDTO) {
        final ChallengeCount challengeCount = challengeCountRepository.findById(challengeCountId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(challengeCountDTO, challengeCount);
        challengeCountRepository.save(challengeCount);
    }

    public void delete(final Long challengeCountId) {
        challengeCountRepository.deleteById(challengeCountId);
    }

    private ChallengeCountDTO mapToDTO(final ChallengeCount challengeCount,
            final ChallengeCountDTO challengeCountDTO) {
        challengeCountDTO.setChallengeCountId(challengeCount.getChallengeCountId());
        challengeCountDTO.setCount(challengeCount.getCount());
        challengeCountDTO.setMember(challengeCount.getMember() == null ? null : challengeCount.getMember().getMemberId());
        challengeCountDTO.setChallenge(challengeCount.getChallenge() == null ? null : challengeCount.getChallenge().getChallengeId());
        return challengeCountDTO;
    }

    private ChallengeCount mapToEntity(final ChallengeCountDTO challengeCountDTO,
            final ChallengeCount challengeCount) {
        challengeCount.setCount(challengeCountDTO.getCount());
        final Member member = challengeCountDTO.getMember() == null ? null : memberRepository.findById(challengeCountDTO.getMember())
                .orElseThrow(() -> new NotFoundException("member not found"));
        challengeCount.setMember(member);
        final Challenge challenge = challengeCountDTO.getChallenge() == null ? null : challengeRepository.findById(challengeCountDTO.getChallenge())
                .orElseThrow(() -> new NotFoundException("challenge not found"));
        challengeCount.setChallenge(challenge);
        return challengeCount;
    }

}
