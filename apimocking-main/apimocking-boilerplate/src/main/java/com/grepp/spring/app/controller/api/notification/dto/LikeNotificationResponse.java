package com.grepp.spring.app.controller.api.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LikeNotificationResponse {
    private Long notificationId;
    private String message;
    private boolean read;
    private Long senderId;
    private String type;
} 