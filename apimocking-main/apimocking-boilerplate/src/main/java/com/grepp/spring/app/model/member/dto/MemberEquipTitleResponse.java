package com.grepp.spring.app.model.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter @Setter @NoArgsConstructor
public class MemberEquipTitleResponse {
    private String equippedTitle;
    private List<String> achievedTitles;
    
    public MemberEquipTitleResponse(String equippedTitle, List<String> achievedTitles) {
        this.equippedTitle = equippedTitle;
        this.achievedTitles = achievedTitles;
    }
} 