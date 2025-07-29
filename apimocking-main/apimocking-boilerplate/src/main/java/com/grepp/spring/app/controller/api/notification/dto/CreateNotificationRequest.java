package com.grepp.spring.app.controller.api.notification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateNotificationRequest {
    @NotNull private Long receiverId;
    private String message;
    @NotNull private Long senderId;
    @NotNull private String type;
    private String senderName; // 동적 메시지 생성을 위한 필드
    private String title;      // 칭호명 등 동적 메시지 생성을 위한 필드
} 