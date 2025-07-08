package com.grepp.spring.app.controller.api.userMain;

import com.grepp.spring.infra.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserMainController {

    // 사용자 평균 절약 금액
    @GetMapping("/average-saving")
    public ApiResponse<Map<String, Object>> getAverageSaving() {
        return ApiResponse.success(Map.of("totalsaving", 406666));
    }

    // 챌린지 달성률 높은 유저
    @GetMapping("/top-challenges")
    public ApiResponse<List<Map<String, Object>>> getTopChallenges() {
        List<Map<String, Object>> users = List.of(
                Map.of("nickname", "user1", "level", 3, "name", "소통왕"), // name = 챌린지 테이블의 칭호명
                Map.of("nickname", "user2", "level", 4, "name", "절약왕")
        );

        return ApiResponse.success(users);
    }

    // 거지왕 TOP3
    @GetMapping("/top-savers")
    public ApiResponse<List<Map<String, Object>>> getTopSavers() {
        List<Map<String, Object>> topSavers = List.of(
                Map.of("name","1만원으로 하루살기"),
                Map.of("name","하루 식비 카테고리 0원 달성"),
                Map.of("name", "오늘 사용한 영수증 인증하기")
        );

        return ApiResponse.success(topSavers);
    }

}
