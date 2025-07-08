package com.grepp.spring.app.model.challenge.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChallengeDTO {

    private Long challengeId;

    @NotNull
    @Size(max = 255)
    private String name;

    @Size(max = 2000)
    private String description;

    @NotNull
    @Size(max = 255)
    private String type;

    private Integer exp;

}
