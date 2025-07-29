package com.grepp.spring.app.controller.api.mock.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CommonResponse {
    private int code;
    private String message;
    private Object data;
} 