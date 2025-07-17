package com.grepp.spring.app.controller.api.mock.notification;

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
    // 알림 생성
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createNotification(@RequestBody CreateNotificationRequest request) {
        // 하드코딩된 ID 반환
        return ResponseEntity.status(201).body(new ApiResponse<>("2001", "성공적으로 생성되었습니다.", 100L));
    }

    // 커뮤니티 좋아요 알림 조회
    @GetMapping("/like")
    public ResponseEntity<ApiResponse<List<LikeNotificationResponse>>> getLikeNotifications() {
        List<LikeNotificationResponse> data = List.of(
            new LikeNotificationResponse(1L, "홍길동님이 회원님의 게시글에 좋아요를 눌렀어요!", false, 2, "LIKE")
        );
        return ResponseEntity.ok(new ApiResponse<>("2000", "성공적으로 처리되었습니다.", data));
    }

    // 커뮤니티 댓글 알림 조회
    @GetMapping("/comment")
    public ResponseEntity<ApiResponse<List<CommentNotificationResponse>>> getCommentNotifications() {
        List<CommentNotificationResponse> data = List.of(
            new CommentNotificationResponse(2L, "김철수님이 회원님의 게시글에 댓글을 달았어요!", false, 3, "COMMENT")
        );
        return ResponseEntity.ok(new ApiResponse<>("2000", "성공적으로 처리되었습니다.", data));
    }

    // 칭호 획득 알림 조회
    @GetMapping("/title")
    public ResponseEntity<ApiResponse<List<TitleNotificationResponse>>> getTitleNotifications() {
        List<TitleNotificationResponse> data = List.of(
            new TitleNotificationResponse(3L, "절약왕 칭호를 획득했어요!", false, "TITLE")
        );
        return ResponseEntity.ok(new ApiResponse<>("2000", "성공적으로 처리되었습니다.", data));
    }

    // 알림 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markNotificationAsRead(@PathVariable Long notificationId) {
        return ResponseEntity.ok(new ApiResponse<>("2000", "성공적으로 처리되었습니다.", null));
    }

    // === 내부 static DTO들 ===
    public static class LikeNotificationResponse {
        public Long notificationId;
        public String message;
        public boolean read;
        public int senderId;
        public String type;
        public LikeNotificationResponse(Long notificationId, String message, boolean read, int senderId, String type) {
            this.notificationId = notificationId;
            this.message = message;
            this.read = read;
            this.senderId = senderId;
            this.type = type;
        }
    }
    public static class CommentNotificationResponse {
        public Long notificationId;
        public String message;
        public boolean read;
        public int senderId;
        public String type;
        public CommentNotificationResponse(Long notificationId, String message, boolean read, int senderId, String type) {
            this.notificationId = notificationId;
            this.message = message;
            this.read = read;
            this.senderId = senderId;
            this.type = type;
        }
    }
    public static class TitleNotificationResponse {
        public Long notificationId;
        public String message;
        public boolean read;
        public String type;
        public TitleNotificationResponse(Long notificationId, String message, boolean read, String type) {
            this.notificationId = notificationId;
            this.message = message;
            this.read = read;
            this.type = type;
        }
    }
    public static class CreateNotificationRequest {
        public Long receiverId;
        public String message;
        public Integer senderId;
        public String type;
        public String senderName;
        public String title;
    }
    public static class ApiResponse<T> {
        public String code;
        public String message;
        public T data;
        public ApiResponse(String code, String message, T data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }
    }
} 