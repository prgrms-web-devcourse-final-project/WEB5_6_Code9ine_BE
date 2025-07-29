package com.grepp.spring.app.controller.api.mock.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateNotificationRequest {
    private Long receiverId;
    private String message;
    private Integer senderId;
    private String type;
    private String senderName;
    private String title;
} 