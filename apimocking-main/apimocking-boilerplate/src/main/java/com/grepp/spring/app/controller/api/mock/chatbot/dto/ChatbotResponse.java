package com.grepp.spring.app.controller.api.mock.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChatbotResponse {
    private int code;
    private String message;
    private Data data;
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Data {
        private String answer;
    }
} 