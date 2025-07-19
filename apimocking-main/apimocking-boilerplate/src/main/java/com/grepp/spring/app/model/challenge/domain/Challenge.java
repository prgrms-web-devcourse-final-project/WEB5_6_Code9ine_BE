package com.grepp.spring.app.model.challenge.domain;

import com.grepp.spring.app.model.achieved_title.domain.AchievedTitle;
import com.grepp.spring.app.model.challenge_count.domain.ChallengeCount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Challenge {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long challengeId;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String type;

    @Column
    private Integer exp;

    @Column
    private String icon;

    @Column
    private Integer total;


    @OneToMany(mappedBy = "challenge")
    private Set<ChallengeCount> challengeCounts = new HashSet<>();

    @OneToMany(mappedBy = "challenge")
    private Set<AchievedTitle> achievedTitles = new HashSet<>();

}
