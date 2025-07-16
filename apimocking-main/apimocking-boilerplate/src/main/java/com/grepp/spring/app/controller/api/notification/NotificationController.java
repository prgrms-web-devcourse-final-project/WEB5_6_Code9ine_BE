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
        @NotNull public Long receiverId;
        @NotNull public String message;
        @NotNull public Integer senderId;
        @NotNull public String type;
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
        Long id = notificationService.createNotification(request.receiverId, request.message, request.senderId, request.type);
        return ResponseEntity.status(201).body(new ApiResponse<>("2001", "성공적으로 생성되었습니다.", id));
    }

    // 커뮤니티 좋아요 알림 조회
    @GetMapping("/like")
    @Operation(summary = "커뮤니티 좋아요 알림 조회", description = "좋아요 알림 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<LikeNotificationResponse>>> getLikeNotifications(@Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        String memberId = extractMemberIdFromToken(token);
        List<LikeNotificationResponse> data = notificationService.getLikeNotifications(memberId);
        return ResponseEntity.ok(new ApiResponse<>("2000", "성공적으로 처리되었습니다.", data));
    }

    // 커뮤니티 댓글 알림 조회
    @GetMapping("/comment")
    @Operation(summary = "커뮤니티 댓글 알림 조회", description = "댓글 알림 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CommentNotificationResponse>>> getCommentNotifications(@Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        String memberId = extractMemberIdFromToken(token);
        List<CommentNotificationResponse> data = notificationService.getCommentNotifications(memberId);
        return ResponseEntity.ok(new ApiResponse<>("2000", "성공적으로 처리되었습니다.", data));
    }

    // 칭호 획득 알림 조회
    @GetMapping("/title")
    @Operation(summary = "칭호 획득 알림 조회", description = "칭호 알림 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<TitleNotificationResponse>>> getTitleNotifications(@Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        String memberId = extractMemberIdFromToken(token);
        List<TitleNotificationResponse> data = notificationService.getTitleNotifications(memberId);
        return ResponseEntity.ok(new ApiResponse<>("2000", "성공적으로 처리되었습니다.", data));
    }

    // 알림 읽음 처리
    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리", description = "알림을 읽음 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> markNotificationAsRead(@PathVariable Long notificationId, @Parameter(hidden = true) @RequestHeader("Authorization") String token) {
        String memberId = extractMemberIdFromToken(token);
        notificationService.markNotificationAsRead(notificationId, Long.parseLong(memberId));
        return ResponseEntity.ok(new ApiResponse<>("2000", "성공적으로 처리되었습니다.", null));
    }

    // 토큰에서 memberId 추출
    private String extractMemberIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        Principal principal = (Principal) authentication.getPrincipal();
        return principal.getMemberId().toString();
    }
} 