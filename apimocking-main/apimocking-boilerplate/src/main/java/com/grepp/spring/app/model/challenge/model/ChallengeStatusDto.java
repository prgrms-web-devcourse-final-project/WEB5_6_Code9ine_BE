package com.grepp.spring.app.model.challenge.model;

import com.grepp.spring.app.model.challenge.domain.Challenge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeStatusDto {

    private Long challengeId;
    private String name;
    private String description;
    private String type;
    private int total;
    private int progress;
    private String icon;
}
