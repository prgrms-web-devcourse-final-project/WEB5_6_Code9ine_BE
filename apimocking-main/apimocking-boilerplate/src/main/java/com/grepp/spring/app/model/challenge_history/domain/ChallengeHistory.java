package com.grepp.spring.app.model.challenge_history.domain;

import com.grepp.spring.app.model.challenge.domain.Challenge;
import com.grepp.spring.app.model.community.domain.CommunityPost;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.infra.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Setter;

@Entity
@Setter
public class ChallengeHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private CommunityPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    private Challenge challenge;

}
