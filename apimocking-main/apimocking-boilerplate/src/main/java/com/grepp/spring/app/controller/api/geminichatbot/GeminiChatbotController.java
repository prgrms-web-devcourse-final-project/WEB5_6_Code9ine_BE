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
                "response1", "하루에 최대 3번까지만 요청할 수 있습니다.",
                "response2", "하루에 최대 3번까지만 요청할 수 있습니다.",
                "response3", "하루에 최대 3번까지만 요청할 수 있습니다.",
                "response4", "하루에 최대 3번까지만 요청할 수 있습니다.",
                "response5", "하루에 최대 3번까지만 요청할 수 있습니다.",
                "response6", "하루에 최대 3번까지만 요청할 수 있습니다."
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
                "response1", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.",
                "response2", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.",
                "response3", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.",
                "response4", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.",
                "response5", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.",
                "response6", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요."
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
                "\n\n아래는 전체 사용자의 카테고리별 평균 지출입니다.\n" +
                averageData +
                "\n\n다음 6가지 질문에 대해 각각 한 문단씩 답변해주세요. 특히 3번 질문에서는 구체적인 금액 비교를 해주세요:\n" +
                "1. 내가 제일 돈 많이 쓰는 건 어떤 분야 같니?\n" +
                "2. 내 수입보다 많이 지출한 분야 뭐야?\n" +
                "3. 다른 사람들보다 내가 많이 쓴 분야는 뭐야? (예: '전체 사용자 평균 식비 지출은 100,000원이고 사용자의 식비 지출액은 150,000원으로 평균보다 50,000원 더 많이 쓰셨어요')\n" +
                "4. 절약 꿀팁이 뭐가 있어?\n" +
                "5. 소비에서 절약할 수 있는 카테고리는 뭐야?\n" +
                "6. 내가 어디서 돈을 아낄 수 있을 것 같니?\n" +
                "각 답변은 반드시 '1.', '2.', '3.', '4.', '5.', '6.'으로 시작하는 줄로 구분해서 출력해줘.";
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
            if (trimmed.startsWith("1.")) result.put("response1", trimmed.substring(2).trim());
            else if (trimmed.startsWith("2.")) result.put("response2", trimmed.substring(2).trim());
            else if (trimmed.startsWith("3.")) result.put("response3", trimmed.substring(2).trim());
            else if (trimmed.startsWith("4.")) result.put("response4", trimmed.substring(2).trim());
            else if (trimmed.startsWith("5.")) result.put("response5", trimmed.substring(2).trim());
            else if (trimmed.startsWith("6.")) result.put("response6", trimmed.substring(2).trim());
        }
        // 혹시 누락된 항목이 있으면 기본 메시지로 채움
        if (!result.containsKey("response1")) result.put("response1", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.");
        if (!result.containsKey("response2")) result.put("response2", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.");
        if (!result.containsKey("response3")) result.put("response3", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.");
        if (!result.containsKey("response4")) result.put("response4", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.");
        if (!result.containsKey("response5")) result.put("response5", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.");
        if (!result.containsKey("response6")) result.put("response6", "Gemini 응답 생성에 실패했습니다. 다시 시도해주세요.");
        return result;
    }
} 