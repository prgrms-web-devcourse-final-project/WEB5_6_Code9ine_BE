package com.grepp.spring.app.controller.api.mock.challenge;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;
import java.util.List;
import java.util.Map;

@RestController
@Profile("mock")
@RequestMapping(value = "/api/challenges", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChallengeMockController {
    // 챌린지 대시보드 조회
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        DashboardResponse.Data data = new DashboardResponse.Data(10, 3, 5, Map.of(
                "weekly", Map.of("total", 3, "completed", 1),
                "monthly", Map.of("total", 2, "completed", 0)
        ));
        return ResponseEntity.ok(new DashboardResponse(2000, "챌린지 목록을 조회했습니다.", data));
    }

    // 챌린지 전체 목록 조회
    @GetMapping
    public ResponseEntity<ChallengeListResponse> getChallenges() {
        ChallengeListResponse.Data data = new ChallengeListResponse.Data(List.of(
                Map.of("challenged", 1, "title", "하루 0원 챌린지", "category", "일반", "description", "만원으로 하루를 살아보자!")
        ));
        return ResponseEntity.ok(new ChallengeListResponse(2000, "챌린지 목록을 조회했습니다.", data));
    }

    // 챌린지 참가/취소
    @PostMapping("/{challengeId}/join")
    public ResponseEntity<CommonResponse> joinChallenge(@PathVariable Long challengeId) {
        return ResponseEntity.ok(new CommonResponse(2000, "챌린지에 참가했습니다.", Map.of("challenged", challengeId)));
    }
    @DeleteMapping("/{challengeId}/join")
    public ResponseEntity<CommonResponse> leaveChallenge(@PathVariable Long challengeId) {
        return ResponseEntity.ok(new CommonResponse(2000, "챌린지 참가를 취소했습니다.", Map.of("challenged", challengeId)));
    }

    // 챌린지 인증
    @GetMapping("/{challengeId}/status")
    public ResponseEntity<StatusResponse> getChallengeStatus(@PathVariable Long challengeId) {
        StatusResponse.Data data = new StatusResponse.Data(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
        return ResponseEntity.ok(new StatusResponse(2000, "챌린지 인증 현황을 조회했습니다.", data));
    }
    @GetMapping("/{challengeId}/proof")
    public ResponseEntity<ProofResponse> getChallengeProof(@PathVariable Long challengeId) {
        ProofResponse.Data data = new ProofResponse.Data("receiptUrl", "오늘 0원 달성!");
        return ResponseEntity.ok(new ProofResponse(2000, "챌린지 인증 내역을 조회했습니다.", data));
    }

    // 내부 static DTO들
    public static class DashboardResponse {
        public int code;
        public String message;
        public Data data;
        public DashboardResponse(int code, String message, Data data) { this.code = code; this.message = message; this.data = data; }
        public static class Data {
            public int daily;
            public int completed;
            public int weekly;
            public Map<String, Map<String, Integer>> progress;
            public Data(int daily, int completed, int weekly, Map<String, Map<String, Integer>> progress) {
                this.daily = daily; this.completed = completed; this.weekly = weekly; this.progress = progress;
            }
        }
    }
    public static class ChallengeListResponse {
        public int code;
        public String message;
        public Data data;
        public ChallengeListResponse(int code, String message, Data data) { this.code = code; this.message = message; this.data = data; }
        public static class Data {
            public List<Map<String, Object>> challenges;
            public Data(List<Map<String, Object>> challenges) { this.challenges = challenges; }
        }
    }
    public static class CommonResponse {
        public int code;
        public String message;
        public Object data;
        public CommonResponse(int code, String message, Object data) { this.code = code; this.message = message; this.data = data; }
    }
    public static class StatusResponse {
        public int code;
        public String message;
        public Data data;
        public StatusResponse(int code, String message, Data data) { this.code = code; this.message = message; this.data = data; }
        public static class Data {
            public int progressRate;
            public Data(int... progress) { this.progressRate = 50; }
        }
    }
    public static class ProofResponse {
        public int code;
        public String message;
        public Data data;
        public ProofResponse(int code, String message, Data data) { this.code = code; this.message = message; this.data = data; }
        public static class Data {
            public String receiptUrl;
            public String memo;
            public Data(String receiptUrl, String memo) { this.receiptUrl = receiptUrl; this.memo = memo; }
        }
    }
} 