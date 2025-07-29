package com.grepp.spring.app.controller.api.mock.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TitleNotificationResponse {
    private Long notificationId;
    private String message;
    private boolean read;
    private String type;
} 