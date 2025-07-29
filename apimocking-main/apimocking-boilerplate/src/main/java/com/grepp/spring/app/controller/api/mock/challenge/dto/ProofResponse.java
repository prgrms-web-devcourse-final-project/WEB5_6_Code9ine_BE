package com.grepp.spring.app.controller.api.mock.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ProofResponse {
    private int code;
    private String message;
    private Data data;
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Data {
        private String receiptUrl;
        private String memo;
    }
} 