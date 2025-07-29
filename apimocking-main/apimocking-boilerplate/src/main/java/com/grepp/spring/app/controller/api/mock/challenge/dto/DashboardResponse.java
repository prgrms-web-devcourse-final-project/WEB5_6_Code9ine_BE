package com.grepp.spring.app.controller.api.mock.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DashboardResponse {
    private int code;
    private String message;
    private Data data;
    
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Data {
        private int daily;
        private int completed;
        private int weekly;
        private Map<String, Map<String, Integer>> progress;
    }
} 