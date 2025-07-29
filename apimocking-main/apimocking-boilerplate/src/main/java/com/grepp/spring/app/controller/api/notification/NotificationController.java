package com.grepp.spring.app.controller.api.notification;

import com.grepp.spring.app.model.notification.service.NotificationService;
import com.grepp.spring.app.controller.api.notification.dto.CreateNotificationRequest;
import com.grepp.spring.app.controller.api.notification.dto.LikeNotificationResponse;
import com.grepp.spring.app.controller.api.notification.dto.CommentNotificationResponse;
import com.grepp.spring.app.controller.api.notification.dto.TitleNotificationResponse;
import com.grepp.spring.infra.response.ApiResponse;
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



    // 알림 생성
    @PostMapping
    @Operation(summary = "알림 생성", description = "알림을 생성합니다.")
    public ResponseEntity<ApiResponse<Long>> createNotification(@RequestBody CreateNotificationRequest request) {
        NotificationService.NotificationCreateRequest serviceRequest = 
            new NotificationService.NotificationCreateRequest(
                request.getReceiverId(), 
                request.getSenderId(), 
                request.getType(), 
                request.getMessage(), 
                request.getSenderName(), 
                request.getTitle()
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

    // 특정 타입의 모든 알림 읽음 처리
    @PatchMapping("/{type}/read-all")
    @Operation(summary = "전체 알림 읽음 처리", description = "특정 타입의 모든 알림을 읽음 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> markAllNotificationsAsReadByType(
            @PathVariable String type, 
            @AuthenticationPrincipal Principal principal) {
        notificationService.markAllNotificationsAsReadByType(principal.getMemberId(), type);
        return ResponseEntity.ok(new ApiResponse<>("2000", "성공적으로 처리되었습니다.", null));
    }
} 