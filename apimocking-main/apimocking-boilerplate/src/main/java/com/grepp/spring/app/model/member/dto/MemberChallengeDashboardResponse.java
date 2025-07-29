package com.grepp.spring.app.model.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MemberChallengeDashboardResponse {
    @Schema(description = "챌린지 목록")
    private List<ChallengeData> challenges;
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ChallengeData {
        @Schema(description = "챌린지 ID", example = "1")
        private Long challengeId;
        
        @Schema(description = "챌린지 이름", example = "만원의 행복")
        private String name;
        
        @Schema(description = "챌린지 타입", example = "일일")
        private String type;
        
        @Schema(description = "챌린지 설명", example = "만원으로 하루 살아보기")
        private String description;
        
        @Schema(description = "총 목표", example = "1")
        private Integer total;
        
        @Schema(description = "진행률", example = "0")
        private Integer progress;
        
        @Schema(description = "아이콘", example = "moneyIcon")
        private String icon;
    }
} 