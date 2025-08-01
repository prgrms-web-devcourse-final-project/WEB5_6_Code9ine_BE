package com.grepp.spring.app.controller.api.mock.chatbot;

import com.grepp.spring.app.controller.api.mock.chatbot.dto.ChatbotRequest;
import com.grepp.spring.app.controller.api.mock.chatbot.dto.ChatbotResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;
import java.util.Map;

@RestController
@Profile("mock")
@RequestMapping(value = "/api/chatbot", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatbotMockController {
    // 자산 관리 챗봇 질문 응답
    @PostMapping("/asset")
    public ResponseEntity<ChatbotResponse> assetChatbot(@RequestBody ChatbotRequest request) {
        ChatbotResponse.Data data = new ChatbotResponse.Data("자산 관리를 잘 하려면 가계부 앱과 맞춤형 예산표를 이용하거나 통장을 쪼개는 것이 좋아요!");
        return ResponseEntity.ok(new ChatbotResponse(2000, "자산 관리 답변을 조회했습니다.", data));
    }


} 