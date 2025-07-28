package com.grepp.spring.app.controller.api.notification;

import com.grepp.spring.app.model.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.grepp.spring.infra.auth.jwt.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import com.grepp.spring.app.model.auth.domain.Principal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "알림 API", description = "알림 관련 API")
public class NotificationController {
    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    public NotificationController(NotificationService notificationService, JwtTokenProvider jwtTokenProvider) {
        this.notificationService = notificationService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // DTO 정의
    public static class LikeNotificationResponse {
        public Long notificationId;
        public String message;
        public boolean read;
        public Long senderId;
        public String type;
        public LikeNotificationResponse(Long notificationId, String message, boolean read, Long senderId, String type) {
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
        public Long senderId;
        public String type;
        public CommentNotificationResponse(Long notificationId, String message, boolean read, Long senderId, String type) {
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
        public Long aTId; // 획득한 칭호의 aTId 추가
        public TitleNotificationResponse(Long notificationId, String message, boolean read, String type, Long aTId) {
            this.notificationId = notificationId;
            this.message = message;
            this.read = read;
            this.type = type;
            this.aTId = aTId;
        }
    }
    public static class CreateNotificationRequest {
        @NotNull public Long receiverId;
        public String message;
        @NotNull public Long senderId;
        @NotNull public String type;
        public String senderName; // 동적 메시지 생성을 위한 필드
        public String title;      // 칭호명 등 동적 메시지 생성을 위한 필드
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

    // 알림 생성
    @PostMapping
    @Operation(summary = "알림 생성", description = "알림을 생성합니다.")
    public ResponseEntity<ApiResponse<Long>> createNotification(@RequestBody CreateNotificationRequest request) {
        NotificationService.NotificationCreateRequest serviceRequest = 
            new NotificationService.NotificationCreateRequest(
                request.receiverId, 
                request.senderId, 
                request.type, 
                request.message, 
                request.senderName, 
                request.title
            );
        Long id = notificationService.createNotification(serviceRequest);
        return ResponseEntity.status(201).body(new ApiResponse<>("2001", "성공적으로 생성되었습니다.", id));
    }

    // 커뮤니티 좋아요 알림 조회
    @GetMapping("/like")
    @Operation(summary = "커뮤니티 좋아요 알림 조회", description = "좋아요 알림 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<LikeNotificationResponse>>> getLikeNotifications(@AuthenticationPrincipal Principal principal) {
        List<LikeNotificationResponse> data = notificationService.getLikeNotifications(principal.getMemberId());
        return ResponseEntity.ok(new ApiResponse<>("2000", "성공적으로 처리되었습니다.", data));
    }

    // 커뮤니티 댓글 알림 조회
    @GetMapping("/comment")
    @Operation(summary = "커뮤니티 댓글 알림 조회", description = "댓글 알림 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CommentNotificationResponse>>> getCommentNotifications(@AuthenticationPrincipal Principal principal) {
        List<CommentNotificationResponse> data = notificationService.getCommentNotifications(principal.getMemberId());
        return ResponseEntity.ok(new ApiResponse<>("2000", "성공적으로 처리되었습니다.", data));
    }

    // 칭호 획득 알림 조회
    @GetMapping("/title")
    @Operation(summary = "칭호 획득 알림 조회", description = "칭호 알림 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<TitleNotificationResponse>>> getTitleNotifications(@AuthenticationPrincipal Principal principal) {
        List<TitleNotificationResponse> data = notificationService.getTitleNotifications(principal.getMemberId());
        return ResponseEntity.ok(new ApiResponse<>("2000", "성공적으로 처리되었습니다.", data));
    }

    // 알림 읽음 처리
    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리", description = "알림을 읽음 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> markNotificationAsRead(@PathVariable Long notificationId, @AuthenticationPrincipal Principal principal) {
        notificationService.markNotificationAsRead(notificationId, principal.getMemberId());
        return ResponseEntity.ok(new ApiResponse<>("2000", "성공적으로 처리되었습니다.", null));
    }
} 