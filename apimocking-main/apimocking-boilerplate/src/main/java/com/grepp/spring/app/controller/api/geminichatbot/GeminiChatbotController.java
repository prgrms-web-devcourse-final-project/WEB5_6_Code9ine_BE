package com.grepp.spring.app.controller.api.geminichatbot;

import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.geminichatbot.service.GeminiChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.Map;
import java.io.IOException;

@RestController
@RequestMapping("/api/geminichatbot")
@Tag(name = "Gemini 챗봇", description = "Google Gemini 기반 자산관리 챗봇 API")
public class GeminiChatbotController {
    private final GeminiChatbotService geminiChatbotService;
    @Autowired
    public GeminiChatbotController(GeminiChatbotService geminiChatbotService) {
        this.geminiChatbotService = geminiChatbotService;
    }

    @PostMapping("/analyze")
    @Operation(summary = "자산관리 Gemini 챗봇 분석", description = "로그인 사용자의 최근 한달 지출 데이터를 기반으로 Gemini API를 호출해 소비 분석, 절약 피드백, 지출 줄이기 팁을 제공한다.")
    public ResponseEntity<Map<String, String>> analyze(@AuthenticationPrincipal Principal principal) {
        try {
            Long memberId = principal.getMemberId();
            String spendingData = geminiChatbotService.getSpendingDataForLastMonth(memberId);
            String prompt = buildGeminiPrompt(spendingData);
            String geminiResponse = callGemini(prompt);
            String message = extractGeminiText(geminiResponse);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("message", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요."));
        }
    }

    private static String buildGeminiPrompt(String spendingData) {
        return "아래는 사용자의 최근 한 달 지출 내역입니다.\n" +
                spendingData +
                "\n1. 전체 소비 패턴을 요약해줘.\n2. 절약 정도에 대한 피드백을 줘.\n3. 지출이 많은 부분을 줄일 수 있는 구체적인 팁을 알려줘.";
    }

    private static String callGemini(String prompt) throws IOException, InterruptedException {
        String apiKey = "AIzaSyDvCXZ9xn9KCNUIznjqKWXSZVT5QWWWxG8";
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey;
        String requestBody = "{\"contents\":[{\"parts\":[{\"text\":\"" + prompt.replace("\"", "\\\"") + "\"}]}]}";
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private static String extractGeminiText(String geminiResponse) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(geminiResponse);
            return root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();
        } catch (Exception e) {
            return "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.";
        }
    }
} 