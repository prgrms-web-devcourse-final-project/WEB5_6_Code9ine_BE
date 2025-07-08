package com.grepp.spring.app.model.challenge_count.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChallengeCountDTO {

    private Long challengeCountId;

    @NotNull
    private Long challengId;

    @NotNull
    private Long memberId;

    @NotNull
    private Integer count;

    @NotNull
    private Long member;

    @NotNull
    private Long challenge;

}
