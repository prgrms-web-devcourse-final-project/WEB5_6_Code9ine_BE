package com.grepp.spring.app.model.member.dto;

import com.grepp.spring.app.model.achieved_title.model.AchievedTitleDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor
public class MemberMypageResponse {
    private Data data;
    
    public MemberMypageResponse(Data data) {
        this.data = data;
    }
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Data {
        private Long memberId;
        private String email;
        private String name;
        private String nickname;
        private String profileImage;
        private int level;
        private int currentExp;
        private int nextLevelExp;
        private int expProgress;
        private List<Map<String, Object>> myPosts;
        private String goalStuff;
        private BigDecimal remainPrice;
        private List<Map<String, Object>> bookmarkedPosts;
        private List<Map<String, Object>> bookmarkedPlaces;
        private AchievedTitleDTO equippedTitle;
        private List<Map<String, Object>> achievedTitles;
    }
} 