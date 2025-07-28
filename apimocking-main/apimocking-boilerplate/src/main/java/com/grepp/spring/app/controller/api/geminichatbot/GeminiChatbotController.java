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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.io.IOException;
import java.time.Duration;

@RestController
@RequestMapping("/api/geminichatbot")
@Tag(name = "Gemini 챗봇", description = "Google Gemini 기반 자산관리 챗봇 API")
public class GeminiChatbotController {
    private final GeminiChatbotService geminiChatbotService;
    private final String geminiApiKey;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public GeminiChatbotController(GeminiChatbotService geminiChatbotService,
                                   @Value("${gemini.api.key}") String geminiApiKey,
                                   RedisTemplate<String, Object> redisTemplate) {
        this.geminiChatbotService = geminiChatbotService;
        this.geminiApiKey = geminiApiKey;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/analyze")
    @Operation(summary = "자산관리 Gemini 챗봇 분석", description = "로그인 사용자의 최근 한달 지출 데이터를 기반으로 Gemini API를 호출해 소비 분석, 절약 피드백, 지출 줄이기 팁을 제공한다. (하루 최대 3회)")
    public ResponseEntity<Map<String, String>> analyze(@AuthenticationPrincipal Principal principal) {
        Long memberId = principal.getMemberId();
        
        // 요청 횟수 체크
        if (!checkAndIncrementRequestCount(memberId)) {
            return ResponseEntity.ok(Map.of(
                "highestSpendingCategory", "하루에 최대 3번까지만 요청할 수 있습니다.",
                "overIncomeSpending", "하루에 최대 3번까지만 요청할 수 있습니다.",
                "aboveAverageSpending", "하루에 최대 3번까지만 요청할 수 있습니다.",
                "savingTips", "하루에 최대 3번까지만 요청할 수 있습니다.",
                "savableCategories", "하루에 최대 3번까지만 요청할 수 있습니다.",
                "moneySavingAreas", "하루에 최대 3번까지만 요청할 수 있습니다."
            ));
        }
        
        try {
            String spendingData = geminiChatbotService.getSpendingDataForLastMonth(memberId);
            String averageData = geminiChatbotService.getAverageSpendingByCategory();
            String prompt = buildGeminiPrompt(spendingData, averageData);
            String geminiResponse = callGemini(prompt, geminiApiKey);
            String message = extractGeminiText(geminiResponse);
            Map<String, String> messages = splitGeminiMessages(message);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "highestSpendingCategory", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.",
                "overIncomeSpending", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.",
                "aboveAverageSpending", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.",
                "savingTips", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.",
                "savableCategories", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.",
                "moneySavingAreas", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요."
            ));
        }
    }

    private boolean checkAndIncrementRequestCount(Long memberId) {
        String today = java.time.LocalDate.now().toString();
        String key = "gemini:user:" + memberId + ":" + today;
        
        String count = (String) redisTemplate.opsForValue().get(key);
        int currentCount = count == null ? 0 : Integer.parseInt(count);
        
        if (currentCount >= 3) {
            return false; // 3번 초과
        }
        
        // 횟수 증가
        redisTemplate.opsForValue().set(key, String.valueOf(currentCount + 1), 
                                       Duration.ofHours(24));
        return true; // 요청 가능
    }

    private static String buildGeminiPrompt(String spendingData, String averageData) {
        return "아래는 사용자의 최근 한 달 지출 내역입니다.\n" +
                spendingData +
                "\n\n전체 사용자 카테고리별 평균 지출:\n" +
                averageData +
                "\n\n다음 질문에 답변해주세요:\n" +
                "1. 어떤 분야에서 가장 많은 돈을 쓰고 있나요?\n" +
                "2. 어떤 분야에서 수입보다 더 많은 돈을 쓰고 있나요?\n" +
                "3. 어떤 분야에서 다른 사람들보다 더 많은 돈을 쓰고 있나요? (예: '평균 사용자 식비는 10만원이고, 당신의 식비는 15만원으로 평균보다 5만원 더 많이 씁니다')\n" +
                "4. 절약 팁을 알려주세요.\n" +
                "5. 어떤 카테고리에서 절약할 수 있을까요?\n" +
                "6. 어디서 돈을 절약할 수 있을까요?\n" +
                "각 답변은 반드시 '1.', '2.', '3.', '4.', '5.', '6.'으로 시작하는 줄로 구분해서 출력해주세요.";
    }

    private static String callGemini(String prompt, String apiKey) throws IOException, InterruptedException {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
        String requestBody = "{\"contents\":[{\"parts\":[{\"text\":\"" + prompt.replace("\"", "\\\"") + "\"}]}]}";
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("X-Goog-Api-Key", apiKey)
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

    private static Map<String, String> splitGeminiMessages(String geminiText) {
        Map<String, String> result = new java.util.HashMap<>();
        String[] parts = geminiText.split("(?=\\n?\\d\\.)"); // "1.", "2.", "3.", "4.", "5.", "6." 앞에서 분리
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.startsWith("1.")) result.put("highestSpendingCategory", trimmed.substring(2).trim());
            else if (trimmed.startsWith("2.")) result.put("overIncomeSpending", trimmed.substring(2).trim());
            else if (trimmed.startsWith("3.")) result.put("aboveAverageSpending", trimmed.substring(2).trim());
            else if (trimmed.startsWith("4.")) result.put("savingTips", trimmed.substring(2).trim());
            else if (trimmed.startsWith("5.")) result.put("savableCategories", trimmed.substring(2).trim());
            else if (trimmed.startsWith("6.")) result.put("moneySavingAreas", trimmed.substring(2).trim());
        }
        // 혹시 누락된 항목이 있으면 기본 메시지로 채움
        if (!result.containsKey("highestSpendingCategory")) result.put("highestSpendingCategory", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.");
        if (!result.containsKey("overIncomeSpending")) result.put("overIncomeSpending", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.");
        if (!result.containsKey("aboveAverageSpending")) result.put("aboveAverageSpending", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.");
        if (!result.containsKey("savingTips")) result.put("savingTips", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.");
        if (!result.containsKey("savableCategories")) result.put("savableCategories", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.");
        if (!result.containsKey("moneySavingAreas")) result.put("moneySavingAreas", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.");
        return result;
    }
} 