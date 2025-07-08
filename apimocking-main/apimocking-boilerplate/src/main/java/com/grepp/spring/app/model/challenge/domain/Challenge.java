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

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Long challengeId;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String type;

    @Column
    private Integer exp;

    @OneToMany(mappedBy = "challenge")
    private Set<ChallengeCount> challengeCounts = new HashSet<>();

    @OneToMany(mappedBy = "challenge")
    private Set<AchievedTitle> achievedTitles = new HashSet<>();

}
