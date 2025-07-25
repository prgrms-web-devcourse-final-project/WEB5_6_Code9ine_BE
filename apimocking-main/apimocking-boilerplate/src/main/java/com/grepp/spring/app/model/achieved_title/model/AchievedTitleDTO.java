package com.grepp.spring.app.model.achieved_title.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AchievedTitleDTO {

    @JsonProperty("aTId")
    private Long aTId;

    private Long challengeId;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    private Boolean achieved;

    private Integer minCount;

    private String icon;

    @NotNull
    private Long challenge;

    public AchievedTitleDTO(Long challengeId, String name, Integer minCount, String icon) {

        this.challengeId = challengeId;
        this.name = name;
        this.minCount = minCount;
        this.icon = icon;
    }
    public AchievedTitleDTO() {}
}
