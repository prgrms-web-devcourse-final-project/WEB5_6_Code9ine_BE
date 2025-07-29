package com.grepp.spring.app.model.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class MemberTitleResponse {
    private Long aTId;
    private String name;
    private Boolean achieved;
    private Integer minCount;
    private Long challengeId;
    private String challengeName;
    
    public MemberTitleResponse(Long aTId, String name, Boolean achieved, Integer minCount, Long challengeId, String challengeName) {
        this.aTId = aTId;
        this.name = name;
        this.achieved = achieved;
        this.minCount = minCount;
        this.challengeId = challengeId;
        this.challengeName = challengeName;
    }
} 