package com.grepp.spring.app.controller.api.notification;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;
import java.util.List;
import java.util.Map;

@RestController
@Profile("mock")
@RequestMapping(value = "/api/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationMockController {
    // 알림 목록 조회
    @GetMapping
    public ResponseEntity<NotificationListResponse> getNotifications() {
        NotificationListResponse.Data data = new NotificationListResponse.Data(List.of(
                Map.of("notificationId", 1, "message", "이번 달 외식비 지출이 많아요!", "read", false)
        ));
        return ResponseEntity.ok(new NotificationListResponse(2000, "알림 목록을 조회했습니다.", data));
    }

    // 알림 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<CommonResponse> readNotification(@PathVariable Long notificationId) {
        return ResponseEntity.ok(new CommonResponse(2000, "해 시계모양이 바뀌었어요!", Map.of("notificationId", notificationId)));
    }

    // 내부 static DTO들
    public static class NotificationListResponse {
        public int code;
        public String message;
        public Data data;
        public NotificationListResponse(int code, String message, Data data) { this.code = code; this.message = message; this.data = data; }
        public static class Data {
            public List<Map<String, Object>> notifications;
            public Data(List<Map<String, Object>> notifications) { this.notifications = notifications; }
        }
    }
    public static class CommonResponse {
        public int code;
        public String message;
        public Object data;
        public CommonResponse(int code, String message, Object data) { this.code = code; this.message = message; this.data = data; }
    }
} 