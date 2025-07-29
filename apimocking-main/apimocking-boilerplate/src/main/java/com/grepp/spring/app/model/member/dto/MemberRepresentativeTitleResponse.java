package com.grepp.spring.app.model.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class MemberRepresentativeTitleResponse {
    private Long aTId;
    
    public MemberRepresentativeTitleResponse(Long aTId) {
        this.aTId = aTId;
    }
} 